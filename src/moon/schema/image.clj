(ns moon.schema.image
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.schemas :as schemas]
            [moon.textures :as textures]))

(defn malli-form [_ schemas]
  (schemas/create-map-schema schemas
                             [:image/file
                              [:image/bounds {:optional true}]]))

; too many ! too big ! scroll ... only show files first & preview?
; make tree view from folders, etc. .. !! all creatures animations showing...
#_(defn- texture-rows [ctx]
    (for [file (sort (assets/all-of-type assets :texture))]
      [(image-button/create {:texture-region (texture/region (assets file))
                             :skin skin})]
      #_[(text-button/create file
                             (fn [_actor _ctx]))]))

(defn create
  [schema image {:keys [ctx/skin
                        ctx/textures]}]
  (let [texture-region (textures/texture-region textures image)]
    (image-button/create (doto (texture-region-drawable/create texture-region)
                           (drawable/set-min-size! (* 2 (texture-region/width texture-region))
                                                   (* 2 (texture-region/height texture-region))))))
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here
