(ns gdx.scenes.scene2d.ui.text-button
  (:require [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.actor.set-opts :as actor]))

(defn create
  [{:keys [text skin] :as opts}]
  (doto (text-button/create text skin)
    (actor/set-opts! opts)))
