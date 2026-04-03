(ns moon.image-button
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [clj.api.com.badlogic.gdx.scenes.scene2d.event :as event]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.actor :as actor]
            [moon.stage :as stage]))

(defn create
  [{:keys [drawable/texture-region
           on-clicked
           drawable/scale
           skin]}]
  (let [scale (or scale 1)
        [w h] [(texture-region/width  texture-region)
               (texture-region/height texture-region)]
        drawable (doto (texture-region-drawable/create texture-region)
                   (drawable/set-min-size! (* scale w) (* scale h)))
        image-button (image-button/create drawable)]
    (when on-clicked
      (actor/add-listener! image-button (change-listener/create
                                          (fn [event actor]
                                            (on-clicked actor (stage/ctx (event/stage event)))))))
    image-button))
