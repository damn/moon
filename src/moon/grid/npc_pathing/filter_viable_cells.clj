(ns moon.grid.npc-pathing.filter-viable-cells
  (:require [moon.cell.is-pf-blocked :as pf-blocked?]
            [moon.cell.is-occupied-by-other :as occupied-by-other?]
            [moon.grid.npc-pathing.remove-not-allowed-diagonals :as remove-not-allowed-diagonals]))

; not using filter because nil cells considered @ remove-not-allowed-diagonals
; TODO only non-nil cells check
; TODO always called with cached-adjacent-cells ...
(defn f [eid adjacent-cells]
  (remove-not-allowed-diagonals/f
   (mapv #(when-not (or (pf-blocked?/f @%)
                        (occupied-by-other?/f @% eid))
            %)
         adjacent-cells)))
