(ns editor.create-widget.image
  (:require [scene2d.ui.scroll-pane :as scroll-pane]
            [scene2d.ui.image-button :as image-button]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [texture-region.get-region-height :refer [get-region-height]]
            [texture-region.get-region-width :refer [get-region-width]]
            [moon.textures :as textures]))

; too many ! too big ! scroll ... only show files first & preview?
; make tree view from folders, etc. .. !! all creatures animations showing...
#_(defn- texture-rows [ctx]
    (for [file (sort (assets/all-of-type assets :texture))]
      [(image-button/create {:texture-region (region/f (assets file))
                             :skin skin})]
      #_[(text-button/create file
                             (fn [_actor _ctx]))]))

(defn f
  [schema image {:keys [ctx/skin
                        ctx/textures]}]
  (image-button/create
   (let [texture-region (textures/texture-region textures image)
         scale 2]
     (doto (texture-region-drawable/f texture-region)
       (set-min-size!/f (* scale (get-region-width texture-region))
                        (* scale (get-region-height texture-region))))))
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here
