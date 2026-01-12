(ns moon.ui ; TODO delete and upfactor!? its just 'stage' ? ctx.stage ? (moon.ctx.stage protocols?)
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport]
            [clojure.repl]
            [moon.ui.action-bar :as action-bar]
            [moon.ui.inventory :as inventory-window]
            [moon.ui.message :as message]
            [moon.ui.text-button :as text-button]
            [moon.ui.utils :as ui-utils]
            [moon.ui.widgets :as widgets]
            [moon.ui.window :as window]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)
           (com.badlogic.gdx.utils.viewport Viewport)
           (moon Stage)))

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

(defn- inventory-cell-with-item? [^Actor actor]
  (and (.getParent actor)
       (= "inventory-cell" (.getName (.getParent actor)))
       (.getUserObject (.getParent actor))))

(defn create!
  [{:keys [graphics/batch
           graphics/ui-viewport]}]
  (Stage. ui-viewport batch))

(defn- toggle-visible! [^Actor actor]
  (.setVisible actor (not (.isVisible actor))))

(defn- stage-find [stage k]
  (-> stage
      .getRoot
      (.findActor k)))

(extend-type moon.Stage
  UserInterface
  (dispose! [_]
    ; TODO fixme skin ?
    )

  (show-data-viewer! [this data skin]
    (.addActor this
               (widgets/data-viewer-window
                {:title "Data View"
                 :data data
                 :width 500
                 :height 500
                 :skin skin})))

  (viewport-width  [stage]
    (Viewport/.getWorldWidth (.getViewport stage)))

  (viewport-height [stage]
    (Viewport/.getWorldHeight (.getViewport stage)))

  (get-ctx [this]
    (.ctx this))

  (mouseover-actor [this position]
    (let [[x y] (viewport/unproject (.getViewport this) position)]
      (.hit this x y true)))

  (action-bar-selected-skill [this]
    (-> this
        .getRoot
        (.findActor "moon.ui.action-bar")
        action-bar/selected-skill))

  (inventory-window-visible? [stage]
    (-> stage
        (stage-find "moon.ui.windows")
        (.findActor "moon.ui.windows.inventory")
        Actor/.isVisible))

  (toggle-inventory-visible! [stage]
    (-> stage
        (stage-find "moon.ui.windows")
        (.findActor "moon.ui.windows.inventory")
        toggle-visible!))

  ; no window movable type cursor appears here like in player idle
  ; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
  ; => input events handling
  ; hmmm interesting ... can disable @ item in cursor  / moving / etc.
  (show-modal-window! [stage skin ui-viewport {:keys [title text button-text on-click]}]
    (assert (not (-> stage
                     .getRoot
                     (.findActor "moon.ui.modal-window"))))
    (.addActor stage
               (window/create
                {:title title
                 :rows [[{:actor (Label. text ^Skin skin)}]
                        [{:actor (text-button/create
                                  {:text button-text
                                   :on-clicked (fn [_actor _ctx]
                                                 (.remove
                                                  (-> stage
                                                      .getRoot
                                                      (.findActor "moon.ui.modal-window")))
                                                 (on-click))
                                   :skin skin})}]]
                 :actor/name "moon.ui.modal-window"
                 :modal? true
                 :actor/center-position [(/ (Viewport/.getWorldWidth  ui-viewport) 2)
                                         (* (Viewport/.getWorldHeight ui-viewport) (/ 3 4))]
                 :pack? true})))

  (set-item! [stage cell item-properties skin]
    (-> stage
        (stage-find "moon.ui.windows")
        (.findActor "moon.ui.windows.inventory")
        (inventory-window/set-item! cell item-properties skin)))

  (remove-item! [stage inventory-cell]
    (-> stage
        (stage-find "moon.ui.windows")
        (.findActor "moon.ui.windows.inventory")
        (inventory-window/remove-item! inventory-cell)))

  (add-skill! [stage skill-properties skin]
    (-> stage
        .getRoot
        (.findActor "moon.ui.action-bar")
        (action-bar/add-skill! skill-properties skin)))

  (remove-skill! [stage skill-id]
    (-> stage
        .getRoot
        (.findActor "moon.ui.action-bar")
        (action-bar/remove-skill! skill-id)))

  (show-text-message! [stage message]
    (-> stage
        .getRoot
        (.findActor "player-message")
        (message/show! message)))

  (toggle-entity-info-window! [stage]
    (-> stage
        (stage-find "moon.ui.windows")
        (.findActor "moon.ui.windows.entity-info")
        toggle-visible!))

  (close-all-windows! [stage]
    (->> (stage-find stage "moon.ui.windows")
         .getChildren
         (run! #(Actor/.setVisible % false))))

  (show-error-window! [stage skin throwable]
    (.addActor stage
               (window/create
                {:title "Error"
                 :rows [[{:actor (Label. (binding [*print-level* 3]
                                           (utils/with-err-str
                                             (clojure.repl/pst throwable)))
                                         ^Skin skin)}]]
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
