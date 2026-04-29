(ns moon.application.create.unorganised
  (:require [clojure.input :as input]
            [clojure.gdx.scene2d.stage :as stage]
            clojure.gdx.scene2d.ui.data-viewer-window
            clojure.gdx.scene2d.ui.error-window
            clojure.gdx.scene2d.ui.horizontal-group
            clojure.gdx.scene2d.ui.image-button
            clojure.gdx.scene2d.ui.scroll-pane
            clojure.gdx.scene2d.ui.stack
            clojure.gdx.scene2d.ui.text-button
            clojure.gdx.scene2d.ui.widget
            clojure.gdx.scene2d.ui.window
            moon.ui.property-editor-window
            moon.ui.property-overview-window
            [moon.body :as body]
            [moon.skill :as skill]
            [moon.state :as state]
            [moon.textures :as textures]))

(defn step [ctx]
  (assoc ctx
          ; frame
          :ctx/active-entities nil
          :ctx/delta-time nil
          :ctx/ui-mouse-position nil
          :ctx/world-mouse-position nil
          ;
          :ctx/mouseover-eid nil
          ; graphics
          :ctx/unit-scale (atom 1)
          ;
          ;
          ; world
          :ctx/elapsed-time 0
          :ctx/paused? false
          :ctx/factions-iterations {:good 15 :evil 5}
          ; TODO if it doens't change put in defs?
          :ctx/max-delta 0.04
          :ctx/minimum-size 0.39
          :ctx/z-orders [:z-order/on-ground
                         :z-order/ground
                         :z-order/flying
                         :z-order/effect]
          ;
          :ctx/potential-field-cache (atom nil)
          :ctx/id-counter (atom 0)
          :ctx/entity-ids (atom {})
         ))

(defmethod state/draw-ui-view :player-item-on-cursor
  [_ eid {:keys [ctx/input
                 ctx/stage
                 ctx/textures
                 ctx/ui-mouse-position]}]
  ; TODO see player-item-on-cursor at render layers
  ; always draw it here at right position, then render layers does not need input/stage
  ; can pass world to graphics, not handle here at application
  (when (stage/mouseover-actor stage (input/mouse-position input))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
      ui-mouse-position
      {:center? true}]]))

; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.

(comment
 (.postRunnable com.badlogic.gdx.Gdx/app
                (fn []
                  (:tx/show-modal @moon.application/state
                       {:title "TestTitle"
                        :text "TextTEXT"
                        :button-text "testbuttonTEXT"
                        :on-click (fn [])})))

 )
