(ns scene2d.actor
  (:require [clojure.gdx.actor.new :as new-actor]))

(defn f
  [{:keys [act! draw!]}]
  (new-actor/f (or act!
                   (fn [_actor _delta]))
               (or draw!
                   (fn [_actor _batch _parent-alpha]))))
