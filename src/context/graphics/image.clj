(ns context.graphics.image
  (:require [api.context :as ctx]
            [api.graphics :as g])
  (:import [com.badlogic.gdx.graphics Color Texture]
           [com.badlogic.gdx.graphics.g2d Batch TextureRegion]))

(defrecord Image [texture-region
                  pixel-dimensions
                  world-unit-dimensions
                  color ; optional
                  ])

(defn- draw-texture-region [^Batch batch texture-region [x y] [w h] rotation color]
  (if color (.setColor batch color))
  (.draw batch texture-region
         x
         y
         (/ (float w) 2) ; rotation origin
         (/ (float h) 2)
         w ; width height
         h
         1 ; scaling factor
         1
         rotation)
  (if color (.setColor batch Color/WHITE)))

(defn- unit-dimensions [image unit-scale]
  (if (= unit-scale 1) ; TODO hardcoded gui-unit-scale .......
    (:pixel-dimensions image)
    (:world-unit-dimensions image)))

(defn- texture-region-dimensions [^TextureRegion texture-region]
  [(.getRegionWidth  texture-region)
   (.getRegionHeight texture-region)])

(defn- scale-dimensions [dimensions scale]
  (mapv (comp float (partial * scale)) dimensions))

(defn- assoc-dimensions
  "scale can be a number for multiplying the texture-region-dimensions or [w h]."
  [{:keys [texture-region] :as image} g scale]
  {:pre [(or (number? scale)
             (and (vector? scale)
                  (number? (scale 0))
                  (number? (scale 1))))]}
  (let [pixel-dimensions (if (number? scale)
                           (scale-dimensions (texture-region-dimensions texture-region) scale)
                           scale)]
    (assoc image
           :pixel-dimensions pixel-dimensions
           :world-unit-dimensions (scale-dimensions pixel-dimensions (g/world-unit-scale g)))))

(defn- ->image [g texture-region]
  (-> {:texture-region texture-region}
      (assoc-dimensions g 1)
      map->Image))

(extend-type api.graphics.Graphics
  api.graphics/Image
  (draw-image [{:keys [batch unit-scale]}
               {:keys [texture-region color] :as image}
               position]
    (draw-texture-region batch
                         texture-region
                         position
                         (unit-dimensions image unit-scale)
                         0 ; rotation
                         color))

  (draw-rotated-centered-image [{:keys [batch unit-scale]}
                                {:keys [texture-region color] :as image}
                                rotation
                                [x y]]
    (let [[w h] (unit-dimensions image unit-scale)]
      (draw-texture-region batch
                           texture-region
                           [(- (float x) (/ (float w) 2))
                            (- (float y) (/ (float h) 2))]
                           [w h]
                           rotation
                           color)))

  (draw-centered-image [this image position]
    (g/draw-rotated-centered-image this image 0 position)))

(extend-type api.context.Context
  api.context/Images
  (create-image [{g :context/graphics :as ctx} file]
    (->image g (TextureRegion. (ctx/cached-texture ctx file))))

  (get-sub-image [{g :context/graphics} {:keys [texture-region]} [x y w h]]
    (->image g (TextureRegion. texture-region (int x) (int y) (int w) (int h))))

  (spritesheet [ctx file tilew tileh]
    {:image (ctx/create-image ctx file)
     :tilew tilew
     :tileh tileh})

  (get-sprite [ctx {:keys [image tilew tileh]} [x y]]
    (ctx/get-sub-image ctx
                       image
                       [(* x tilew) (* y tileh) tilew tileh])))