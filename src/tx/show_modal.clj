(ns tx.show-modal
  (:require [clojure.gdx.scene2d.actor :refer [remove!
                                               set-position!
                                               set-name!]]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [gdx.scenes.scene2d.ui.label :as label]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.window :as window]
            [clojure.gdx.scene2d.group.find-actor :refer [find-actor]]
            [clojure.gdx.scene2d.stage.add-actor :refer [add-actor!]]
            [clojure.gdx.utils.align :as align]))

(defn f
  [{:keys [ctx/skin
           ctx/stage]
    :as ctx}
   {:keys [title text button-text on-click]}]
  (assert (not (find-actor (:stage/root stage) "moon.ui.modal-window")))
  (add-actor! stage
              (doto (window/create
                     {:title title
                      :skin skin
                      :window/modal? true
                      :table/rows [[{:actor (label/create
                                             {:text text
                                              :skin skin})}]
                                   [{:actor (doto (text-button/create {:text button-text :skin skin})
                                              (add-listener! (change-listener/create
                                                              (fn [_event _actor]
                                                                (remove! (find-actor (:stage/root stage) "moon.ui.modal-window"))
                                                                (on-click)))))}]]})
                (set-name! "moon.ui.modal-window")
                (set-position! [(/ (:viewport/world-width (:stage/viewport stage)) 2)
                                (* (:viewport/world-height (:stage/viewport stage)) (/ 3 4))]
                               align/center)))
  nil)

; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.

(comment
 (.postRunnable com.badlogic.gdx.Gdx/app
                (fn []
                  (:tx/show-modal @dev.application/state
                                  {:title "TestTitle"
                                   :text "TextTEXT"
                                   :button-text "testbuttonTEXT"
                                   :on-click (fn [])})))

 )
