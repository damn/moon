(ns clojure.gdx.scenes.scene2d.ui.image
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.ui.actor :as actor]
            [moon.ui.image]))

(defmethod actor/create :ui/image
  [{:keys [content] :as opts}]
  (doto (image/create (if (map? content)
                        (texture-region-drawable/create content)
                        content))
    (actor/set-opts! opts)))

(extend-type com.badlogic.gdx.scenes.scene2d.ui.Image
  moon.ui.image/Image
  (set-drawable! [image drawable]
    (image/set-drawable! image (texture-region-drawable/create drawable))))
