/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics.routing.filter;

import ballantines.nautics.Leg;

/**
 *
 * @author mbuse
 */
public interface LegFilter {
  
  boolean accept(Leg leg);
}
