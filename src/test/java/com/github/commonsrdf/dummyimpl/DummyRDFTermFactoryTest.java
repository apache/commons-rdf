package com.github.commonsrdf.dummyimpl;

import com.github.commonsrdf.api.AbstractRDFTermFactoryTest;
import com.github.commonsrdf.api.RDFTermFactory;

public class DummyRDFTermFactoryTest extends AbstractRDFTermFactoryTest {

	@Override
	public RDFTermFactory createFactory() {
		return new DummyRDFTermFactory();
	}

}
