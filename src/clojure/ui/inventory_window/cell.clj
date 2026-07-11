(ns clojure.ui.inventory-window.cell
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.math.vector2 :as gdx-vector2] [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]
            [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]
            [com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]))

(defn ->cell [do! draw! on-click-cell slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (stack/new)]
       (run! #(group/addActor stack %)
             [(widget/new
               (fn [this _batch _parent-alpha]
                 (when-let [stage (actor/getStage this)]
                   (let [{:keys [ctx/player-eid
                                 ctx/ui-mouse-position]
                          :as ctx} (:stage/ctx stage)]
                     (draw! ctx
                            (draw-cell-rect @player-eid
                                            (actor/getX this)
                                            (actor/getY this)
                                            (let [[mx my] ui-mouse-position
                                                  [x y] (vector2/clojurize
                                                         (actor/stageToLocalCoordinates this
                                                                                          (gdx-vector2/new mx my)))]
                                              (actor/hit this x y true))
                                            (actor/getUserObject (actor/getParent this))))))))
              (doto (image/newDrawable background-drawable)
                (actor/setName "image-widget")
                (actor/setUserObject {:background-drawable background-drawable
                                    :cell-size cell-size}))])
       (doto stack
         (actor/addListener (click-listener/create
                          (fn [event _x _y]
                            (let [{:keys [ctx/player-eid]
                                   :as ctx} (:stage/ctx (event/getStage event))]
                              (do! ctx (on-click-cell player-eid cell))))))
         (actor/setName "inventory-cell")
         (actor/setUserObject cell)))}))
