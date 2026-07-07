(ns clojure.records-body
  (:require [qrecord.core :as q]))

(q/defrecord R [
                body/position
                body/width
                body/height
                body/collides?
                body/z-order
                body/rotation-angle
                ])
