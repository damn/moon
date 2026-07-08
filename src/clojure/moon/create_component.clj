(ns clojure.moon.create-component
  (:require [clojure.moon.create-component.animation :as animation]
            [clojure.moon.create-component.body :as body]
            [clojure.moon.create-component.delete-after-duration :as delete-after-duration]
            [clojure.moon.create-component.projectile-collision :as projectile-collision]
            [clojure.moon.create-component.stats :as stats]))

(def k->create
  {:entity/animation animation/f
   :entity/body body/f
   :entity/delete-after-duration delete-after-duration/f
   :entity/projectile-collision projectile-collision/f
   :entity/stats stats/f})

(defn create-component
  [ctx k v]
  (if-let [f (k->create k)]
    (f v ctx)
    v))
