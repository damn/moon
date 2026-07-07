(ns gdx.scene2d.actor
  (:require [clojure.actor :as actor]))

(defn f
  [{:keys [act! draw!]}]
  (actor/new (or act!
                   (fn [_actor _delta]))
               (or draw!
                   (fn [_actor _batch _parent-alpha]))))
