(ns clojure.gdx.scene2d.ui.text-field
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [moon.actor :as actor]))

(defn create [{:keys [text skin] :as opts}]
  (doto (text-field/create text skin)
    (actor/set-opts! opts)))
