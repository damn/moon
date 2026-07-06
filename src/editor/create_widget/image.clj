(ns editor.create-widget.image
  (:require
            [com.badlogic.gdx.graphics.texture :as texture] [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [clojure.gdx.texture-region.get-region-height :as get-region-height]
            [clojure.gdx.texture-region.get-region-width :as get-region-width]
            [clojure.gdx.texture-region-drawable.new :as new-texture-region-drawable]
            [clojure.gdx.texture-region-drawable.set-min-size :as set-min-size]
            [scene2d.ui.scroll-pane :as scroll-pane]
            [scene2d.ui.text-button :as text-button]
            [moon.textures :as textures]))

; too many ! too big ! scroll ... only show files first & preview?
; make tree view from folders, etc. .. !! all creatures animations showing...
#_(defn- texture-rows [ctx]
    (for [file (sort (assets/all-of-type assets :texture))]
      [(ImageButton. {:texture-region (region/f (assets file))
                             :skin skin})]
      #_[(text-button/create file
                             (fn [_actor _ctx]))]))

(defn f
  [schema image {:keys [ctx/skin
                        ctx/textures]}]
  (let [texture-region (textures/texture-region textures image)
        scale 2]
    (image-button/new
     (doto (new-texture-region-drawable/f texture-region)
       (set-min-size/f (* scale (get-region-width/f texture-region))
                       (* scale (get-region-height/f texture-region))))))
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here
