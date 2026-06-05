#_(ns moon.mapgen-test
  (:require [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [moon.db.build :refer [build]]
            [clojure.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [clojure.maps.tiled.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [clojure.maps.tiled.tiled-map-tile-layer.set-visible :refer [set-visible!]]
            [moon.utils.camera :as camera-utils]))

(def ^:private infotext
  "L: grid lines
  M: movement properties
  zoom: minus,equals
  ESCAPE: leave
  direction keys: move")

#_(defn- map-infos ^String [c]
  (let [tile (mapv int (g/world-mouse-position ctx))
        {:keys [tiled-map
                area-level-grid]} @(current-data)]
    (->> [infotext
          (str "Tile " tile)
          (when-not area-level-grid
            (str "Module " (mapv (comp int /)
                                 (g/world-mouse-position ctx)
                                 [modules/width modules/height])))
          (when area-level-grid
            (let [layer (.get (get-layers this) "creatures")]
              (when-let [cell (get-cell layer tile)]
                (when-let [id (-> cell
                                  cell/tile
                                  get-properties
                                  (.get "id"))]
                  (str "Creature id: " id)))))
          (when area-level-grid
            (let [level (get area-level-grid tile)]
              (when (number? level)
                (str "Area level:" level))))
          (str "Movement properties " (utils.tiled/movement-property tiled-map tile) "\n"
               (apply vector (utils.tiled/movement-properties tiled-map tile)))]
         (remove nil?)
         (str/join "\n"))))

#_(defn- ->info-window []
    (let [label (label/create {:label/text ""})
        window (window/create {:title "Info" :rows [[label]]})]
    (add-actor! window (proxy [Actor] []
                        (act [_delta]
                          (do
                           (.setText label (map-infos %))
                           (.pack window)))))
    (.setPosition window 0 (c/ui-viewport-height ctx)) window))

#_(defn- render-on-map [_context]
  (let [{:keys [tiled-map
                area-level-grid
                start-position
                show-movement-properties
                show-grid-lines]} @(current-data)
        visible-tiles (camera-utils/visible-tiles (:viewport/camera world-viewport))
        [x y] (mapv int (g/world-mouse-position ctx))]
    (graphics/rectangle x y 1 1 :white)
    (when start-position
      (graphics/filled-rectangle (start-position 0) (start-position 1) 1 1 [1 0 1 0.9]))
    (when show-movement-properties
      (doseq [[x y] visible-tiles
              :let [prop (utils.tiled/movement-property tiled-map [x y])]]
        (graphics/filled-circle [(+ x 0.5) (+ y 0.5)] 0.08 :black)
        (graphics/filled-circle [(+ x 0.5) (+ y 0.5)]
                          0.05
                          (case prop
                            "all"   :green
                            "air"   :orange
                            "none"  :red))))
    (when show-grid-lines
      (graphics/grid 0
               0
               (tiled/get-property tiled-map "width")
               (tiled/get-property tiled-map "height") 1 1 [1 1 1 0.5]))))

(def ^:private world-id :worlds/uf-caves)

#_(defn- generate-screen-ctx [c properties]
  (let [{:keys [tiled-map start-position]} (generate-level (build db world-id))
        atom-data (current-data)]
    (disp/dispose! (:tiled-map @atom-data))
    (swap! atom-data assoc
           :tiled-map tiled-map
           ;:area-level-grid area-level-grid
           :start-position start-position)
    (show-whole-map! (:viewport/camera world-viewport) tiled-map)
    (set-visible! (.get (get-layers this) "creatures") true)))

#_(defn ->generate-map-window [c level-id]
    (doto (window/create {:title "Properties"
                          :cell-defaults {:pad 10}
                          :rows [[(label/create {:label/text (with-out-str (pprint (build db level-id)))})]
                                 [(text-button "Generate" #(try (generate-screen-ctx c (build db level-id))
                                                                (catch Throwable t
                                                                  (pretty-pst t)
                                                                  (add-actor! stage (error-window/create skin t))
                                                                  (println t))))]] })
      (.pack)))

(defn render [_]
  #_(moon.graphics/draw-on-world-view @state render-on-map)
  #_(if (input/key-just-pressed? input :l)
      (swap! current-data update :show-grid-lines not))
  #_(if (input/key-just-pressed? input :m)
      (swap! current-data update :show-movement-properties not))
  )

(comment
 (atom {:tiled-map (tmx/load-map modules/file)
        :show-movement-properties false
        :show-grid-lines false})
 )

#_(defn actors [_]
    [(->generate-map-window c world-id)
     (->info-window)])
