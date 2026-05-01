(ns clojure.gdx.scene2d.ui.image
  (:require [badlogic.scene2d.ui.image :as image]
            [clojure.gdx.scene2d.actor :as actor]))

(defmethod actor/create :ui/image
  [{:keys [content] :as opts}]
  (doto (image/create content)
    (actor/set-opts! opts)))

(def set-drawable! image/set-drawable!)
