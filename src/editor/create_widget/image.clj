(ns editor.create-widget.image
  (:require [scene2d.ui.scroll-pane :as scroll-pane]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

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
  (ImageButton.
   (let [^TextureRegion texture-region (textures/texture-region textures image)
         scale 2]
     (doto (TextureRegionDrawable. texture-region)
       (set-min-size!/f (* scale (.getRegionWidth texture-region))
                        (* scale (.getRegionHeight texture-region))))))
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here
