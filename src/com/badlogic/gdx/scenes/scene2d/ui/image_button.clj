(ns com.badlogic.gdx.scenes.scene2d.ui.image-button
  (:require [gdl.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdl.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)))

(defmethod actor/create :ui/image-button
  [{:keys [drawable] :as opts}]
  (doto (ImageButton. (texture-region-drawable/create* drawable))
    (actor/set-opts! opts)))
