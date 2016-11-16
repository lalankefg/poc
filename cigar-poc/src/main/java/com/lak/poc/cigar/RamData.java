// PROJECT : cigar-poc
// PRODUCT : Affluence Connect Transbridge
// CLASS : RamData.java
// ************************************************************************************************
//
// Copyright(C) 2013 Fortunaglobal (PRIVATE) LIMITED
// All rights reserved.
//
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF
// Fortunaglobal(PRIVATE) LIMITED.
//
// This copy of the Source Code is intended for Fortunaglobal (PRIVATE) LIMITED 's internal use only
// and is
// intended for view by persons duly authorized by the management of Fortunaglobal (PRIVATE)
// LIMITED. No
// part of this file may be reproduced or distributed in any form or by any
// means without the written approval of the Management of Fortunaglobal (PRIVATE) LIMITED.
//
// *************************************************************************************************
//
// REVISIONS:
// Author : Lakshitha Matarage
// Date : Oct 27, 2016
// Since : version 1.0
// CLASS : RamData.java
// *************

package com.lak.poc.cigar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class RamData {

	 private static Sigar sigar;
	    private static Map<String, Long> pageFoults;

	    public RamData(Sigar s) throws SigarException {
	        sigar = s;
	        System.out.println(getMetric().toString());
	    }

	    public static void main(String[] args) throws SigarException,
	            InterruptedException {
	        new RamData(new Sigar());
	        RamData.startMetricTest();
	    }

	    public static void startMetricTest() throws SigarException,
	            InterruptedException {
	        while (true) {
	            Map<String, String> map = RamData.getMetric("" + sigar.getPid());
	            System.out.println("Resident: \t\t"
	                    + Sigar.formatSize(Long.valueOf(map.get("Resident"))));
	            System.out.println("PageFaults: \t\t" + map.get("PageFaults"));
	            System.out.println("PageFaultsTotal:\t" + map.get("PageFaultsTotal"));
	            System.out.println("Size:    \t\t"
	                    + Sigar.formatSize(Long.valueOf(map.get("Size"))));
	            Map<String, String> map2 = getMetric();
	            for (Entry<String, String> e : map2.entrySet()) {
	                String s;
	                try {
	                    s = Sigar.formatSize(Long.valueOf(e.getValue()));
	                } catch (NumberFormatException ex) {
	                    s = ((int) (double) Double.valueOf(e.getValue())) + "%";
	                }
	                System.out.print("  " + e.getKey() + ": " + s);
	            }
	            System.out.println("\n------------------");
	            Thread.sleep(1000);
	        }
	    }

	    public static Map<String, String> getMetric() throws SigarException {
	        Mem mem = sigar.getMem();
	        return (Map<String, String>) mem.toMap();
	    }

	    public static Map<String, String> getMetric(String pid)
	            throws SigarException {
	        if (pageFoults == null)
	            pageFoults = new HashMap<String, Long>();
	        ProcMem state = sigar.getProcMem(pid);
	        Map<String, String> map = new TreeMap<String, String>(state.toMap());
	        if (!pageFoults.containsKey(pid))
	            pageFoults.put(pid, state.getPageFaults());
	        map.put("PageFaults", ""
	                + (state.getPageFaults() - pageFoults.get(pid)));
	        map.put("PageFaultsTotal", ""+state.getPageFaults());
	        return map;
	    }
}
