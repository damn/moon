(ns clojure.records-body
  (:require [qrecord.core :as q]))

(q/defrecord R [
                ;; :entity/collision-rectangle !
                body/position
                body/width
                body/height
                ;; to entity itself !!
                body/collides?
                body/z-order
                body/rotation-angle
                ])
