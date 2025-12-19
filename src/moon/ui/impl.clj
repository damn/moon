(ns moon.ui.impl
  (:require [clojure.repl]
            [gdl.math.vector2 :as vector2]
            [gdl.ui.actor :as actor]
            [gdl.ui.group :as group]
            [gdl.ui.stage :as stage]
            [gdl.ui.utils :as ui-utils]
            [gdl.utils.viewport :as viewport]
            [moon.ui :as ui]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.inventory :as inventory-window]
            [moon.ui.label :as label]
            [moon.ui.message :as message]
            [moon.ui.text-button :as text-button]
            [moon.utils :as utils]))

(defn- inventory-cell-with-item? [actor]
  (and (actor/parent actor)
       (= "inventory-cell" (actor/name (actor/parent actor)))
       (actor/user-object (actor/parent actor))))

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

(extend-type gdl.ui.Stage
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
        (group/find-actor "moon.ui.action-bar")
        action-bar/selected-skill))

  (inventory-window-visible? [stage]
    (-> stage
        (stage-find "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        actor/visible?))

  (toggle-inventory-visible! [stage]
    (-> stage
        (stage-find "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        toggle-visible!))

  ; no window movable type cursor appears here like in player idle
  ; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
  ; => input events handling
  ; hmmm interesting ... can disable @ item in cursor  / moving / etc.
  (show-modal-window! [stage ui-viewport {:keys [title text button-text on-click]}]
    (assert (not (-> stage
                     stage/root
                     (group/find-actor "moon.ui.modal-window"))))
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
                                                            (group/find-actor "moon.ui.modal-window")))
                                                       (on-click))})}]]
                       :actor/name "moon.ui.modal-window"
                       :modal? true
                       :actor/center-position [(/ (viewport/world-width  ui-viewport) 2)
                                               (* (viewport/world-height ui-viewport) (/ 3 4))]
                       :pack? true}))

  (set-item! [stage cell item-properties]
    (-> stage
        (stage-find "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        (inventory-window/set-item! cell item-properties)))

  (remove-item! [stage inventory-cell]
    (-> stage
        (stage-find "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        (inventory-window/remove-item! inventory-cell)))

  (add-skill! [stage skill-properties]
    (-> stage
        stage/root
        (group/find-actor "moon.ui.action-bar")
        (action-bar/add-skill! skill-properties)))

  (remove-skill! [stage skill-id]
    (-> stage
        stage/root
        (group/find-actor "moon.ui.action-bar")
        (action-bar/remove-skill! skill-id)))

  (show-text-message! [stage message]
    (-> stage
        stage/root
        (group/find-actor "player-message")
        (message/show! message)))

  (toggle-entity-info-window! [stage]
    (-> stage
        (stage-find "moon.ui.windows")
        (group/find-actor "moon.ui.windows.entity-info")
        toggle-visible!))

  (close-all-windows! [stage]
    (->> (stage-find stage "moon.ui.windows")
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
       (ui-utils/window-title-bar? actor) [:mouseover-actor/window-title-bar]
       (ui-utils/button?           actor) [:mouseover-actor/button]
       :else                     [:mouseover-actor/unspecified]))))
