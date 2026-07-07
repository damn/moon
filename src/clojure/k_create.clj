(ns clojure.k-create
  (:require [clojure.k-create.animation :as animation]
            [clojure.k-create.body :as body]
            [clojure.k-create.delete-after-duration :as delete-after-duration]
            [clojure.k-create.projectile-collision :as projectile-collision]
            [clojure.k-create.stats :as stats]))

(def k->create
  {:entity/animation animation/f
   :entity/body body/f
   :entity/delete-after-duration delete-after-duration/f
   :entity/projectile-collision projectile-collision/f
   :entity/stats stats/f})
