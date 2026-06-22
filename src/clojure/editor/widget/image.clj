(ns clojure.editor.widget.image
  (:require [gdl.scroll-pane :as scroll-pane]
            [gdl.image-button :as image-button]
            [gdl.text-button :as text-button]
            [clojure.map.texture-region-drawable :as texture-region-drawable]
            [moon.textures :as textures]))

; too many ! too big ! scroll ... only show files first & preview?
; make tree view from folders, etc. .. !! all creatures animations showing...
#_(defn- texture-rows [ctx]
    (for [file (sort (assets/all-of-type assets :texture))]
      [(image-button/create {:texture-region (region/f (assets file))
                             :skin skin})]
      #_[(text-button/create file
                             (fn [_actor _ctx]))]))

(defn create
  [schema image {:keys [ctx/skin
                        ctx/textures]}]
  (image-button/create
   (texture-region-drawable/create* {:drawable/texture-region (textures/texture-region textures image)
                                     :drawable/scale 2}))
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here
