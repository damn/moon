(ns cdq.ui.impl
  (:require [cdq.ui :as ui]
            [cdq.ui.stage :as stage]
            [cdq.ui.action-bar :as action-bar]
            [cdq.ui.inventory :as inventory-window]
            [cdq.ui.message :as message]
            [moon.scene2d.actor :as actor]
            [moon.scene2d.group :as group]
            [moon.math.vector2 :as vector2]
            [moon.utils.viewport :as viewport]
            [clojure.repl]
            [cdq.ui.text-button :as text-button]
            [moon.utils :as utils]
            [moon.ui.label :as label])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               Label
                                               Window)))

(defn- inventory-cell-with-item? [actor]
  (and (actor/parent actor)
       (= "inventory-cell" (actor/name (actor/parent actor)))
       (actor/user-object (actor/parent actor))))

; FIXME does not work
(defn- window-title-bar?
  "Returns true if the actor is a window title bar."
  [actor]
  (when (instance? Label actor)
    (when-let [p (actor/parent actor)]
      (when-let [p (actor/parent p)]
        (and (instance? Window actor)
             (= (.getTitleLabel ^Window p) actor))))))

(defn- button-class? [actor]
  (some #(= Button %) (supers (class actor))))

(comment
 ; maybe use this?
 (isa? (class actor) Button)
 )

(defn- button?
  "Returns true if the actor or its parent is a button."
  [actor]
  (or (button-class? actor)
      (and (actor/parent actor)
           (button-class? (actor/parent actor)))))

(defn create!
  [{:keys [graphics/batch
           graphics/ui-viewport]}
   config]
  (stage/create ui-viewport batch config))

(defn- toggle-visible! [actor]
  (actor/set-visible! actor (not (actor/visible? actor))))

(defn- stage-find [stage k]
  (-> stage
      stage/root
      (group/find-actor k)))

(extend-type cdq.ui.Stage
  ui/UserInterface
  (dispose! [_]
    ; TODO fixme skin ?
    )

  (show-data-viewer! [this data]
    (stage/add-actor! this
                      {:type :actor/data-viewer
                       :title "Data View"
                       :data data
                       :width 500
                       :height 500}))

  (viewport-width  [stage]
    (viewport/world-width (stage/viewport stage)))

  (viewport-height [stage]
    (viewport/world-height (stage/viewport stage)))

  (get-ctx [this]
    (stage/ctx this))

  (mouseover-actor [this position]
    (let [position (vector2/->clj (viewport/unproject (stage/viewport this) (vector2/->java position)))]
      (stage/hit this position true)))

  (action-bar-selected-skill [this]
    (-> this
        stage/root
        (group/find-actor "cdq.ui.action-bar")
        action-bar/selected-skill))

  (inventory-window-visible? [stage]
    (-> stage
        (stage-find "cdq.ui.windows")
        (group/find-actor "cdq.ui.windows.inventory")
        actor/visible?))

  (toggle-inventory-visible! [stage]
    (-> stage
        (stage-find "cdq.ui.windows")
        (group/find-actor "cdq.ui.windows.inventory")
        toggle-visible!))

  ; no window movable type cursor appears here like in player idle
  ; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
  ; => input events handling
  ; hmmm interesting ... can disable @ item in cursor  / moving / etc.
  (show-modal-window! [stage ui-viewport {:keys [title text button-text on-click]}]
    (assert (not (-> stage
                     stage/root
                     (group/find-actor "cdq.ui.modal-window"))))
    (stage/add-actor! stage
                      {:type :actor/window
                       :title title
                       :rows [[{:actor (label/create text)}]
                              [{:actor (text-button/create
                                        {:text button-text
                                         :on-clicked (fn [_actor _ctx]
                                                       (actor/remove!
                                                        (-> stage
                                                            stage/root
                                                            (group/find-actor "cdq.ui.modal-window")))
                                                       (on-click))})}]]
                       :actor/name "cdq.ui.modal-window"
                       :modal? true
                       :actor/center-position [(/ (viewport/world-width  ui-viewport) 2)
                                               (* (viewport/world-height ui-viewport) (/ 3 4))]
                       :pack? true}))

  (set-item! [stage cell item-properties]
    (-> stage
        (stage-find "cdq.ui.windows")
        (group/find-actor "cdq.ui.windows.inventory")
        (inventory-window/set-item! cell item-properties)))

  (remove-item! [stage inventory-cell]
    (-> stage
        (stage-find "cdq.ui.windows")
        (group/find-actor "cdq.ui.windows.inventory")
        (inventory-window/remove-item! inventory-cell)))

  (add-skill! [stage skill-properties]
    (-> stage
        stage/root
        (group/find-actor "cdq.ui.action-bar")
        (action-bar/add-skill! skill-properties)))

  (remove-skill! [stage skill-id]
    (-> stage
        stage/root
        (group/find-actor "cdq.ui.action-bar")
        (action-bar/remove-skill! skill-id)))

  (show-text-message! [stage message]
    (-> stage
        stage/root
        (group/find-actor "player-message")
        (message/show! message)))

  (toggle-entity-info-window! [stage]
    (-> stage
        (stage-find "cdq.ui.windows")
        (group/find-actor "cdq.ui.windows.entity-info")
        toggle-visible!))

  (close-all-windows! [stage]
    (->> (stage-find stage "cdq.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (show-error-window! [stage throwable]
    (stage/add-actor! stage
                      {:type :actor/window
                       :title "Error"
                       :rows [[{:actor (label/create (binding [*print-level* 3]
                                                       (utils/with-err-str
                                                         (clojure.repl/pst throwable))))}]]
                       :modal? true
                       :close-button? true
                       :close-on-escape? true
                       :center? true
                       :pack? true}))

  (actor-information [_ actor]
    (let [inventory-slot (inventory-cell-with-item? actor)]
      (cond
       inventory-slot            [:mouseover-actor/inventory-cell inventory-slot]
       (window-title-bar? actor) [:mouseover-actor/window-title-bar]
       (button?           actor) [:mouseover-actor/button]
       :else                     [:mouseover-actor/unspecified]))))
