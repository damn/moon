(ns game.mouse
  (:import (com.badlogic.gdx Gdx Input)
           (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Button Label Window)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn mouseover-actor [stage x y]
  (.hit ^Stage stage (float x) (float y) true))

(defn ui-mouse-position [stage]
  (let [v2 (.unproject (.getViewport ^Stage stage)
                       (Vector2. (float (.getX ^Input Gdx/input))
                                 (float (.getY ^Input Gdx/input))))]
    [(.x v2) (.y v2)]))

(defn world-mouse-position [world-viewport]
  (let [v2 (.unproject ^Viewport world-viewport
                       (Vector2. (float (.getX ^Input Gdx/input))
                                 (float (.getY ^Input Gdx/input))))]
    [(.x v2) (.y v2)]))

(defn- button?
  [actor]
  (let [button-class? (fn [a] (some #(= Button %) (supers (class a))))]
    (or (button-class? actor)
        (when-let [parent (.getParent ^Actor actor)]
          (button-class? parent)))))

(defn mouseover-actor-info [actor]
  (let [inventory-slot (and (.getParent ^Actor actor)
                            (= "inventory-cell" (.getName ^Actor (.getParent ^Actor actor)))
                            (.getUserObject ^Actor (.getParent ^Actor actor)))]
    (cond
      inventory-slot
      [:mouseover-actor/inventory-cell inventory-slot]

      (when (instance? Label actor)
        (when-let [p (.getParent ^Actor actor)]
          (when-let [p (.getParent ^Actor p)]
            (and (instance? Window p)
                 (= (.getTitleLabel ^Window p) actor)))))
      [:mouseover-actor/window-title-bar]

      (button? actor)
      [:mouseover-actor/button]

      :else
      [:mouseover-actor/unspecified])))
