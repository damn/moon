(ns gdx.scenes.scene2d.ui.text-button
  (:require [clojure.gdx.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.actor :as actor]))

(defn create
  [{:keys [text skin] :as opts}]
  (doto (text-button/create text skin)
    (actor/set-opts! opts)))
