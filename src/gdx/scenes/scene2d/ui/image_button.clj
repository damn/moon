(ns gdx.scenes.scene2d.ui.image-button
  (:require [clojure.gdx.scene2d.ui.image-button :as image-button]
            [clojure.gdx.scene2d.actor.set-opts :as actor]))

(defn create
  [{:keys [drawable] :as opts}]
  (doto (image-button/create drawable)
    (actor/set-opts! opts)))
