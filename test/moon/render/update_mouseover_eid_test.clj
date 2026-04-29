(ns moon.application.render.update-mouseover-eid-test
  (:require [clojure.test :refer :all]
            #_[moon.application.render.update-mouseover-eid :as update-mouseover-eid]))

; Impossible to test w/o protocols ...
; ctx protocols?
; * ctx/mouseover-actor
; * ctx/point->entities
; & ctx/line-of-sight?
; sort ?
#_(update-mouseover-eid
 {:ctx/input
  :ctx/mouseover-eid
  :ctx/stage
  :ctx/player-eid
  :ctx/grid
  :ctx/raycaster
  :ctx/render-z-order
  :ctx/world-mouse-position
  }
 )
