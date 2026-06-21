(ns editor.widget.image
  (:require [clojure.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [clojure.scenes.scene2d.ui.image-button :as image-button]
            [clojure.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
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
