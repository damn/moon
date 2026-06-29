(ns entity.render.active-skill
  (:require [clojure.math :as math]
            [moon.effect.render :as render]
            [timer.ratio :as ratio]
            [moon.textures :as textures]))

(let [radius (let [tile-size 48
                   image-width 32]
               (/ (/ image-width tile-size) 2))]
  (defn f
    [{:keys [skill effect-ctx counter]}
     entity
     {:keys [ctx/colors
             ctx/elapsed-time
             ctx/textures]
      :as ctx}]
    (let [{:keys [entity/image skill/effects]} skill]
      (concat (let [action-counter-ratio (ratio/f elapsed-time counter)
                    texture-region (textures/texture-region textures image)
                    [x y] (:body/position (:entity/body entity))
                    y (+ (float y)
                         (float (/ (:body/height (:entity/body entity)) 2))
                         (float 0.15))
                    center [x (+ y radius)]]
                [[:draw/filled-circle center radius (:colors/active-skill-circle colors)]
                 [:draw/sector
                  center
                  radius
                  (math/to-radians 90) ; start-angle
                  (math/to-radians (* (float action-counter-ratio) 360)) ; degree
                  (:colors/active-skill-sector colors)]
                 [:draw/texture-region texture-region [(- (float x) radius) y]]])
              (mapcat #(render/f % effect-ctx ctx)  ; update-effect-ctx here too ?
                      effects)))))
