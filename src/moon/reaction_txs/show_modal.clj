(ns moon.reaction-txs.show-modal
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [clj.api.com.badlogic.gdx.utils.align :as align]
            [clj.api.com.badlogic.gdx.utils.viewport :as viewport]
            [moon.actor :as actor]
            [moon.stage :as stage]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (stage/find-actor stage "moon.ui.modal-window")))
  (stage/add-actor! stage
                    (doto (window/create title skin)
                      (table/add! (label/create text skin))
                      (table/row!)
                      (table/add! (doto (text-button/create button-text skin)
                                    (actor/add-listener!
                                     (change-listener/create
                                      (fn [_event _actor]
                                        (actor/remove! (stage/find-actor stage "moon.ui.modal-window"))
                                        (on-click))))))
                      (window/set-modal! true)
                      (widget-group/pack!)
                      (actor/set-name! "moon.ui.modal-window")
                      (actor/set-position! (/ (viewport/world-width  (stage/viewport stage)) 2)
                                           (* (viewport/world-height (stage/viewport stage)) (/ 3 4))
                                           align/center)))
  ctx)


; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.

(comment
 (.postRunnable com.badlogic.gdx.Gdx/app
                (fn []
                  (do! @moon.application/state
                       {:title "TestTitle"
                        :text "TextTEXT"
                        :button-text "testbuttonTEXT"
                        :on-click (fn [])})))

 )
