(ns clojure.gdx.scene2d.ui.image
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [moon.ui.actor :as actor]))

(defmethod actor/create :ui/image
  [{:keys [content] :as opts}]
  (doto (image/create content)
    (actor/set-opts! opts)))

(def set-drawable! image/set-drawable!)
