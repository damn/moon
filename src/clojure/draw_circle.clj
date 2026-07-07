(ns clojure.draw-circle
  (:require [clojure.graphics-shape-drawer :as shape-drawer]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/circle! shape-drawer x y radius))

; this functions is taking a map and
; working only one one key
; also it is doing two things
; so it is doing 3 things
; so maybe we should wrap each ctx accessor in ctx
; so we see what is used together and what is passed where


; That means also we could extend at the 'create' step as we know the key ....

; but also those functions make no sense because
; why not just call shape-drawer yourself ?????
