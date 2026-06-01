(ns gdx.scenes.scene2d.ui.text-field
  (:require [clojure.gdx.scene2d.ui.text-field :as text-field]
            [gdx.scenes.scene2d.actor :as actor]))

(defn create
  [{:keys [text skin] :as opts}]
  (doto (text-field/create text skin)
    (actor/set-opts! opts)))

(defn text [text-field]
  (text-field/text text-field))
