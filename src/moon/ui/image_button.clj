(ns moon.ui.image-button
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [gdl.texture-region :as texture-region]
            [moon.ui.actor :as actor]))

(defn- create-drawable*
  [{:keys [drawable/texture-region drawable/scale]}]
  (doto (texture-region-drawable/create texture-region)
    (drawable/set-min-size! (* scale (texture-region/width texture-region))
                            (* scale (texture-region/height texture-region)))))

(defn create
  [{:keys [drawable] :as opts}]
  (doto (image-button/create (create-drawable* drawable))
    (actor/set-opts! opts)))
