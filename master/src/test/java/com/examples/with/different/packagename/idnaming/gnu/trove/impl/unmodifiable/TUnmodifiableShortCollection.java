/**
 * Copyright (C) 2010-2016 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2008, Robert D. Eden All Rights Reserved.
// Copyright (c) 2009, Jeff Randall All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package com.examples.with.different.packagename.idnaming.gnu.trove.impl.unmodifiable;


//////////////////////////////////////////////////
// THIS IS A GENERATED CLASS. DO NOT HAND EDIT! //
//////////////////////////////////////////////////

////////////////////////////////////////////////////////////
// THIS IS AN IMPLEMENTATION CLASS. DO NOT USE DIRECTLY!  //
// Access to these methods should be through TCollections //
////////////////////////////////////////////////////////////


import com.examples.with.different.packagename.idnaming.gnu.trove.iterator.*;
import com.examples.with.different.packagename.idnaming.gnu.trove.procedure.*;
import com.examples.with.different.packagename.idnaming.gnu.trove.set.*;
import com.examples.with.different.packagename.idnaming.gnu.trove.list.*;
import com.examples.with.different.packagename.idnaming.gnu.trove.function.*;
import com.examples.with.different.packagename.idnaming.gnu.trove.map.*;
import com.examples.with.different.packagename.idnaming.gnu.trove.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Random;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;


public class TUnmodifiableShortCollection implements TShortCollection, Serializable {
	private static final long serialVersionUID = 1820017752578914078L;

	final TShortCollection c;

	public TUnmodifiableShortCollection( TShortCollection c ) {
		if ( c == null )
			throw new NullPointerException();
		this.c = c;
	}

	public int size()                   { return c.size(); }
	public boolean isEmpty() 	        { return c.isEmpty(); }
	public boolean contains( short o )    { return c.contains( o ); }
	public short[] toArray()              { return c.toArray(); }
	public short[] toArray( short[] a )     { return c.toArray( a ); }
	public String toString()            { return c.toString(); }
	public short getNoEntryValue()        { return c.getNoEntryValue(); }
	public boolean forEach( TShortProcedure procedure ) { return c.forEach( procedure ); }

	public TShortIterator iterator() {
		return new TShortIterator() {
			TShortIterator i = c.iterator();

			public boolean hasNext()    { return i.hasNext(); }
			public short next()           { return i.next(); }
			public void remove()        { throw new UnsupportedOperationException(); }
		};
	}

	public boolean add( short e ) { throw new UnsupportedOperationException(); }
	public boolean remove( short o ) { throw new UnsupportedOperationException(); }

	public boolean containsAll( Collection<?> coll ) { return c.containsAll( coll ); }
	public boolean containsAll( TShortCollection coll ) { return c.containsAll( coll ); }
	public boolean containsAll( short[] array ) { return c.containsAll( array ); }

	public boolean addAll( TShortCollection coll ) { throw new UnsupportedOperationException(); }
	public boolean addAll( Collection<? extends Short> coll ) { throw new UnsupportedOperationException(); }
	public boolean addAll( short[] array ) { throw new UnsupportedOperationException(); }

	public boolean removeAll( Collection<?> coll ) { throw new UnsupportedOperationException(); }
	public boolean removeAll( TShortCollection coll ) { throw new UnsupportedOperationException(); }
	public boolean removeAll( short[] array ) { throw new UnsupportedOperationException(); }

	public boolean retainAll( Collection<?> coll ) { throw new UnsupportedOperationException(); }
	public boolean retainAll( TShortCollection coll ) { throw new UnsupportedOperationException(); }
	public boolean retainAll( short[] array ) { throw new UnsupportedOperationException(); }

	public void clear() { throw new UnsupportedOperationException(); }
}