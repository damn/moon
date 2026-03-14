(ns moon.schema.image
  (:require [moon.schemas :as schemas]
            [moon.textures :as textures]
            [moon.image-button :as image-button]))

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
  (image-button/create
   {:drawable/texture-region (textures/texture-region textures image)
    :drawable/scale 2
    :skin skin})
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here
