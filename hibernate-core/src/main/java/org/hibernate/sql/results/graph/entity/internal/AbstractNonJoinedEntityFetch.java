/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.sql.results.graph.entity.internal;

import java.util.BitSet;

import org.hibernate.metamodel.mapping.EntityMappingType;
import org.hibernate.metamodel.mapping.internal.ToOneAttributeMapping;
import org.hibernate.spi.NavigablePath;
import org.hibernate.sql.results.graph.AssemblerCreationState;
import org.hibernate.sql.results.graph.DomainResult;
import org.hibernate.sql.results.graph.DomainResultAssembler;
import org.hibernate.sql.results.graph.Fetch;
import org.hibernate.sql.results.graph.FetchParent;
import org.hibernate.sql.results.graph.FetchParentAccess;
import org.hibernate.sql.results.graph.Fetchable;
import org.hibernate.sql.results.graph.InitializerProducer;
import org.hibernate.sql.results.graph.entity.EntityFetch;
import org.hibernate.sql.results.graph.entity.EntityInitializer;
import org.hibernate.sql.results.graph.internal.ImmutableFetchList;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractNonJoinedEntityFetch implements EntityFetch,
		InitializerProducer<AbstractNonJoinedEntityFetch> {
	private final NavigablePath navigablePath;
	private final ToOneAttributeMapping fetchedModelPart;
	private final FetchParent fetchParent;
	private final DomainResult<?> keyResult;
	private final boolean selectByUniqueKey;

	public AbstractNonJoinedEntityFetch(
			NavigablePath navigablePath,
			ToOneAttributeMapping fetchedModelPart,
			FetchParent fetchParent,
			DomainResult<?> keyResult,
			boolean selectByUniqueKey) {
		this.navigablePath = navigablePath;
		this.fetchedModelPart = fetchedModelPart;
		this.fetchParent = fetchParent;
		this.keyResult = keyResult;
		this.selectByUniqueKey = selectByUniqueKey;
	}

	@Override
	public NavigablePath getNavigablePath() {
		return navigablePath;
	}

	@Override
	public ToOneAttributeMapping getFetchedMapping() {
		return fetchedModelPart;
	}

	@Override
	public ToOneAttributeMapping getEntityValuedModelPart() {
		return fetchedModelPart;
	}

	@Override
	public FetchParent getFetchParent() {
		return fetchParent;
	}

	@Override
	public ImmutableFetchList getFetches() {
		return ImmutableFetchList.EMPTY;
	}

	@Override
	public Fetch findFetch(Fetchable fetchable) {
		return null;
	}

	@Override
	public boolean hasJoinFetches() {
		return false;
	}

	@Override
	public boolean containsCollectionFetches() {
		return false;
	}

	@Override
	public boolean hasTableGroup() {
		return false;
	}

	@Override
	public void collectValueIndexesToCache(BitSet valueIndexes) {
		if ( keyResult != null ) {
			keyResult.collectValueIndexesToCache( valueIndexes );
		}
	}

	@Override
	public EntityMappingType getReferencedMappingType() {
		return fetchedModelPart.getEntityMappingType();
	}

	public DomainResult<?> getKeyResult() {
		return keyResult;
	}

	public boolean isSelectByUniqueKey() {
		return selectByUniqueKey;
	}

	@Override
	public DomainResultAssembler<?> createAssembler(
			FetchParentAccess parentAccess,
			AssemblerCreationState creationState) {
		final EntityInitializer entityInitializer = creationState.resolveInitializer( this, parentAccess, this )
				.asEntityInitializer();
		assert entityInitializer != null;
		return buildEntityAssembler( entityInitializer );
	}

	@Override
	public EntityInitializer createInitializer(
			AbstractNonJoinedEntityFetch resultGraphNode,
			FetchParentAccess parentAccess,
			AssemblerCreationState creationState) {
		return resultGraphNode.createInitializer( parentAccess, creationState );
	}

	@Override
	public abstract EntityInitializer createInitializer(FetchParentAccess parentAccess, AssemblerCreationState creationState);

	protected EntityAssembler buildEntityAssembler(EntityInitializer entityInitializer) {
		return new EntityAssembler( getFetchedMapping().getJavaType(), entityInitializer );
	}
}
