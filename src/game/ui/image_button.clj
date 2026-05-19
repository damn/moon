(ns game.ui.image-button
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdl.scene2d.actor :as actor]))

(defmethod actor/create :ui/image-button
  [{:keys [drawable] :as opts}]
  (doto (image-button/create (texture-region-drawable/create* drawable))
    (actor/set-opts! opts)))
