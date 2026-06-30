(ns editor.create-widget.image
  (:require [clojure.gdx :as gdx]
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
  (gdx/image-button
   (let [texture-region (textures/texture-region textures image)
         scale 2]
     (doto (gdx/texture-region-drawable texture-region)
       (gdx/texture-region-drawable-set-min-size!
        (* scale (gdx/texture-region-get-region-width texture-region))
        (* scale (gdx/texture-region-get-region-height texture-region))))))
  #_(ui/image-button image
                     (fn [_actor ctx]
                       (c/add-actor! ctx (scroll-pane/choose-window (texture-rows ctx))))
                     {:dimensions [96 96]})) ; x2  , not hardcoded here
