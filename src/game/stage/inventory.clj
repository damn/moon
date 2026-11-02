(ns game.stage.inventory
  (:require [lib.textures :as textures])
  (:import (com.badlogic.gdx Gdx)
           (com.badlogic.gdx.graphics Color)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Image Skin Stack Table TextTooltip Widget Window)
           (com.badlogic.gdx.scenes.scene2d.utils ClickListener Drawable TextureRegionDrawable)))

(defn- get-cell [inventory-window cell]
  (->> (.getChildren ^Group (.findActor ^Group inventory-window "inventory-cell-table"))
       (filter #(= (.getUserObject ^Actor %) cell))
       first))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")]
    (.setDrawable ^Image image-widget ^Drawable (:background-drawable (.getUserObject ^Actor image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")
        cell-size (:cell-size (.getUserObject ^Actor image-widget))]
    (.setDrawable ^Image image-widget ^Drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                                  (.setMinSize cell-size cell-size)))
    (.addListener ^Actor cell-widget (TextTooltip. ^String tooltip-text ^Skin skin))
    nil))

(defn- cell [world on-click-cell slot->drawable draw-cell-rect! cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (Stack.)]
       (run! #(.addActor ^Group stack ^Actor %)
             [(proxy [Widget] []
                (draw [batch parent-alpha]
                  (when-let [stage (.getStage ^Actor this)]
                    (draw-cell-rect! @(:world/player-eid @world)
                                     (.getX ^Actor this)
                                     (.getY ^Actor this)
                                     (let [v2 (.unproject (.getViewport ^Stage stage)
                                                         (Vector2. (float (.getX Gdx/input))
                                                                   (float (.getY Gdx/input))))
                                           local (.stageToLocalCoordinates ^Actor this
                                                                           (Vector2. (.x v2) (.y v2)))
                                           x (.x ^Vector2 local)
                                           y (.y ^Vector2 local)]
                                       (.hit ^Actor this (float x) (float y) true))
                                     (.getUserObject ^Actor (.getParent ^Actor this))))))
              (doto (Image. ^Drawable background-drawable)
                (.setName "image-widget")
                (.setUserObject {:background-drawable background-drawable
                                 :cell-size cell-size}))])
       (doto stack
         (.addListener (proxy [ClickListener] []
                         (clicked [event _x _y]
                           (on-click-cell event cell))))
         (.setName "inventory-cell")
         (.setUserObject cell)))}))

(defn- build
  [{:keys [world
           on-click-cell
           draw-cell-rect!
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (TextureRegionDrawable. ^TextureRegion (slot->texture-region slot))
                           (.setMinSize cell-size cell-size)
                           (.tint ^Color (Color. 1 1 1 0.4))))
        ->cell (partial cell world on-click-cell slot->drawable draw-cell-rect! cell-size)
        cell-table (Table.)
        window (Window. "Inventory" ^Skin skin)]
    (doseq [row (concat [[{:actor nil} {:actor nil}
                          (->cell :inventory.slot/helm)
                          (->cell :inventory.slot/necklace)]
                         [{:actor nil}
                          (->cell :inventory.slot/weapon)
                          (->cell :inventory.slot/chest)
                          (->cell :inventory.slot/cloak)
                          (->cell :inventory.slot/shield)]
                         [{:actor nil} {:actor nil}
                          (->cell :inventory.slot/leg)]
                         [{:actor nil}
                          (->cell :inventory.slot/glove)
                          (->cell :inventory.slot/rings :position [0 0])
                          (->cell :inventory.slot/rings :position [1 0])
                          (->cell :inventory.slot/boot)]]
                        (for [y (range 4)]
                          (for [x (range 6)]
                            (->cell :inventory.slot/bag :position [x y]))))]
      (doseq [cell row]
        (.add cell-table ^Actor (:actor cell)))
      (.row cell-table))
    (.pack cell-table)
    (.setName cell-table "inventory-cell-table")
    (let [c (.add window cell-table)]
      (.pad c (float 4)))
    (.row window)
    (.pack window)
    (.setName window "moon.ui.windows.inventory")
    (.setVisible window false)
    (let [[x y] position]
      (.setPosition window (float x) (float y)))
    window))

(defn create
  [on-click-cell draw-cell-rect! skin stage textures world]
  (let [slot->y-sprite-idx #:inventory.slot {:weapon 0
                                             :shield 1
                                             :rings 2
                                             :necklace 3
                                             :helm 4
                                             :cloak 5
                                             :chest 6
                                             :leg 7
                                             :glove 8
                                             :boot 9
                                             :bag 10}
        slot->texture-region (fn [slot]
                               (let [width 48
                                     height 48
                                     sprite-x 21
                                     sprite-y (+ (slot->y-sprite-idx slot) 2)
                                     bounds [(* sprite-x width)
                                             (* sprite-y height)
                                             width
                                             height]]
                                 (textures/texture-region textures
                                                          {:image/file "images/items.png"
                                                           :image/bounds bounds})))
        cell-size 48]
    (build
     {:world world
      :on-click-cell on-click-cell
      :draw-cell-rect! draw-cell-rect!
      :skin skin
      :position [(.getWorldWidth (.getViewport ^Stage stage))
                 (.getWorldHeight (.getViewport ^Stage stage))]
      :slot->texture-region slot->texture-region
      :cell-size cell-size})))
