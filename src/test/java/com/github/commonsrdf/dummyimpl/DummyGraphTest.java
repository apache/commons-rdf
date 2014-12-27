package com.github.commonsrdf.dummyimpl;

import com.github.commonsrdf.api.AbstractGraphTest;
import com.github.commonsrdf.api.RDFTermFactory;

public class DummyGraphTest extends AbstractGraphTest {

	@Override
	public RDFTermFactory createFactory() {
		return new DummyRDFTermFactory();
	}

}
