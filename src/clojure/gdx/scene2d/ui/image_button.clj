(ns clojure.gdx.scene2d.ui.image-button
  (:require [badlogic.scene2d.ui.image-button :as image-button]
            [clojure.gdx.scene2d.utils.drawable :as drawable]
            [clojure.gdx.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [clojure.graphics.texture-region :as texture-region]
            [clojure.gdx.scene2d.actor :as actor]))

(defn- create-drawable*
  [{:keys [drawable/texture-region drawable/scale]}]
  (doto (texture-region-drawable/create texture-region)
    (drawable/set-min-size! (* scale (texture-region/width texture-region))
                            (* scale (texture-region/height texture-region)))))

(defmethod actor/create :ui/image-button
  [{:keys [drawable] :as opts}]
  (doto (image-button/create (create-drawable* drawable))
    (actor/set-opts! opts)))
