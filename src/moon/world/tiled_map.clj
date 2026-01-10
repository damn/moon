(ns moon.world.tiled-map)

(defn spawn-positions
  [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (.get (.getLayers tiled-map) layer-name)]
    (for [x (range (.getWidth layer))
          y (range (.getHeight layer))
          :let [position [x y]
                cell (.getCell layer x y)]
          :when cell
          :let [value (-> cell
                          .getTile
                          .getProperties
                          (.get property-key))]
          :when value]
      [position value])))
