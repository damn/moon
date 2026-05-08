(ns clojure.gdx.scene2d.ui.image
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [moon.ui.actor :as actor]
            moon.ui.image))

(defmethod actor/create :ui/image
  [{:keys [content] :as opts}]
  (doto (image/create content)
    (actor/set-opts! opts)))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.Image
  moon.ui.image/Image
  (set-drawable! [this drawable]
    (image/set-drawable! this drawable)))
