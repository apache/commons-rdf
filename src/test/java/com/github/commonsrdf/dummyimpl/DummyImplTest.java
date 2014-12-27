package com.github.commonsrdf.dummyimpl;

import com.github.commonsrdf.api.AbstractCommonsRDFTest;
import com.github.commonsrdf.api.RDFTermFactory;

public class DummyImplTest extends AbstractCommonsRDFTest {

	@Override
	public RDFTermFactory createFactory() {
		return new DummyRDFTermFactory();
	}

}
