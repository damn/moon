(ns moon.ui
  (:require [clojure.repl]
            [moon.ui.actor :as actor]
            [moon.ui.group :as group]
            [moon.ui.label :as label]
            [moon.ui.stage :as stage]
            [moon.ui.utils :as ui-utils]
            [moon.viewport :as viewport]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.inventory :as inventory-window]
            [moon.ui.message :as message]
            [moon.ui.text-button :as text-button]
            [moon.utils :as utils]))

(defprotocol UserInterface
  (dispose! [_])
  (show-data-viewer! [_ data skin])
  (viewport-width  [_])
  (viewport-height [_])
  (get-ctx [_])
  (mouseover-actor [_ position])
  (action-bar-selected-skill [_])
  (inventory-window-visible? [_])
  (toggle-inventory-visible! [_])
  (show-modal-window! [_ skin ui-viewport {:keys [title text button-text on-click]}])
  (set-item! [_ cell item-properties skin])
  (remove-item! [_ inventory-cell])
  (add-skill! [_ skill-properties skin])
  (remove-skill! [_ skill-id])
  (show-text-message! [_ message])
  (toggle-entity-info-window! [_])
  (close-all-windows! [_])
  (show-error-window! [_ skin throwable])
  (actor-information [_ actor]))

(defn- inventory-cell-with-item? [actor]
  (and (actor/parent actor)
       (= "inventory-cell" (actor/name (actor/parent actor)))
       (actor/user-object (actor/parent actor))))

(defn create!
  [{:keys [graphics/batch
           graphics/ui-viewport]}]
  (stage/create ui-viewport batch))

(defn- toggle-visible! [actor]
  (actor/set-visible! actor (not (actor/visible? actor))))

(defn- stage-find [stage k]
  (-> stage
      stage/root
      (group/find-actor k)))

(extend-type moon.Stage
  UserInterface
  (dispose! [_]
    ; TODO fixme skin ?
    )

  (show-data-viewer! [this data skin]
    (stage/add-actor! this
                      (stage/build
                       {:type :actor/data-viewer
                        :title "Data View"
                        :data data
                        :width 500
                        :height 500
                        :skin skin})))

  (viewport-width  [stage]
    (viewport/world-width (stage/viewport stage)))

  (viewport-height [stage]
    (viewport/world-height (stage/viewport stage)))

  (get-ctx [this]
    (stage/ctx this))

  (mouseover-actor [this position]
    (let [position (viewport/unproject (stage/viewport this) position)]
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
  (show-modal-window! [stage skin ui-viewport {:keys [title text button-text on-click]}]
    (assert (not (-> stage
                     stage/root
                     (group/find-actor "moon.ui.modal-window"))))
    (stage/add-actor! stage
                      (stage/build
                       {:type :actor/window
                        :title title
                        :rows [[{:actor (label/create text skin)}]
                               [{:actor (text-button/create
                                         {:text button-text
                                          :on-clicked (fn [_actor _ctx]
                                                        (actor/remove!
                                                         (-> stage
                                                             stage/root
                                                             (group/find-actor "moon.ui.modal-window")))
                                                        (on-click))
                                          :skin skin})}]]
                        :actor/name "moon.ui.modal-window"
                        :modal? true
                        :actor/center-position [(/ (viewport/world-width  ui-viewport) 2)
                                                (* (viewport/world-height ui-viewport) (/ 3 4))]
                        :pack? true})))

  (set-item! [stage cell item-properties skin]
    (-> stage
        (stage-find "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        (inventory-window/set-item! cell item-properties skin)))

  (remove-item! [stage inventory-cell]
    (-> stage
        (stage-find "moon.ui.windows")
        (group/find-actor "moon.ui.windows.inventory")
        (inventory-window/remove-item! inventory-cell)))

  (add-skill! [stage skill-properties skin]
    (-> stage
        stage/root
        (group/find-actor "moon.ui.action-bar")
        (action-bar/add-skill! skill-properties skin)))

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

  (show-error-window! [stage skin throwable]
    (stage/add-actor! stage
                      (stage/build
                       {:type :actor/window
                        :title "Error"
                        :rows [[{:actor (label/create (binding [*print-level* 3]
                                                        (utils/with-err-str
                                                          (clojure.repl/pst throwable)))
                                                      skin)}]]
                        :modal? true
                        :close-button? true
                        :close-on-escape? true
                        :center? true
                        :pack? true})))

  (actor-information [_ actor]
    (let [inventory-slot (inventory-cell-with-item? actor)]
      (cond
       inventory-slot            [:mouseover-actor/inventory-cell inventory-slot]
       (ui-utils/window-title-bar? actor) [:mouseover-actor/window-title-bar]
       (ui-utils/button?           actor) [:mouseover-actor/button]
       :else                     [:mouseover-actor/unspecified]))))
