(ns clojure.k-create
  (:require [clojure.create-animation :as create-animation]
            [clojure.create-body :as create-body]
            [clojure.create-delete-after-duration :as create-delete-after-duration]
            [clojure.create-projectile-collision :as create-projectile-collision]
            [clojure.create-stats :as create-stats]))

(def k->create
  {:entity/animation create-animation/f
   :entity/body create-body/f
   :entity/delete-after-duration create-delete-after-duration/f
   :entity/projectile-collision create-projectile-collision/f
   :entity/stats create-stats/f})
