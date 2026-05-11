(ns moon.ui.impl.actor
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.utils.align :as align]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Touchable)
           (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener
                                                  ClickListener)))

(defn- ui-type->class [k]
  (case k
    :ui/window com.badlogic.gdx.scenes.scene2d.ui.Window))

(defn- button-class? [actor]
  (some #(= com.badlogic.gdx.scenes.scene2d.ui.Button %) (supers (class actor))))

(extend-type Actor
  actor/Actor
  (name [actor]
    (.getName actor))

  (x [actor]
    (.getX actor))

  (y [actor]
    (.getY actor))

  (width [actor]
    (.getWidth actor))

  (height [actor]
    (.getHeight actor))

  (user-object [actor]
    (.getUserObject actor))

  (stage [actor]
    (.getStage actor))

  (set-name! [actor name]
    (.setName actor name))

  (set-user-object! [actor object]
    (.setUserObject actor object))

  (set-position!
    ([actor [x y]]
     (.setPosition actor x y))
    ([actor x y align]
     (.setPosition actor x y (align/k->value align))))

  (set-visible! [actor visible?]
    (.setVisible actor visible?))

  (set-touchable! [actor touchable]
    (.setTouchable actor (case touchable
                           :touchable/disabled Touchable/disabled)))

  (visible? [actor]
    (.isVisible actor))

  (hit [actor [x y] touchable?]
    (.hit actor x y touchable?))

  (remove! [actor]
    (.remove actor))

  (parent [actor]
    (.getParent actor))

  (add-listener! [actor [listener-k listener-params]]
    (.addListener actor
                  (case listener-k
                    :listener/change (let [f listener-params]
                                       (proxy [ChangeListener] []
                                         (changed [event actor]
                                           (f event actor))))
                    :listener/text-tooltip (let [[tooltip skin] listener-params]
                                             (TextTooltip. ^String tooltip ^Skin skin))
                    :listener/click (let [f listener-params]
                                      (proxy [ClickListener] []
                                        (clicked [event x y]
                                          (f event x y)))))))

  (stage->local-coordinates [actor xy]
    (vector2/->clj (.stageToLocalCoordinates actor (vector2/->java xy))))

  (find-ancestor [actor ui-type-k]
    (if-let [parent (actor/parent actor)]
      (if (instance? (ui-type->class ui-type-k) parent)
        parent
        (actor/find-ancestor parent ui-type-k))
      (throw (Error. (str "Actor has no parent window " actor)))))

  (button? [actor]
    (or (button-class? actor)
        (and (actor/parent actor)
             (button-class? (actor/parent actor)))))

  ; FIXME does not work
  (window-title-bar? [actor]
    (when (instance? com.badlogic.gdx.scenes.scene2d.ui.Label actor)
      (when-let [p (actor/parent actor)]
        (when-let [p (actor/parent p)]
          (and (instance? com.badlogic.gdx.scenes.scene2d.ui.Window actor)
               (= (window/title-label p) actor))))))

  (toggle-visible! [actor]
    (actor/set-visible! actor (not (actor/visible? actor))))

  (set-opts! [actor opts]
    (when-let [user-object (:actor/user-object opts)]
      (actor/set-user-object! actor user-object))

    (when (:actor/position opts)
      (let [[x y align] (:actor/position opts)]
        (if align
          (actor/set-position! actor x y align)
          (actor/set-position! actor [x y]))))

    (when (contains? opts :actor/visible?)
      (actor/set-visible! actor (:actor/visible? opts)))

    (when-let [touchable (:actor/touchable opts)]
      (actor/set-touchable! actor touchable))

    (when-let [name (:actor/name opts)]
      (actor/set-name! actor name))

    (when-let [listeners (:actor/listeners opts)]
      (doseq [listener listeners]
        (actor/add-listener! actor listener)))))

(defmethod actor/create :ui/actor
  [{:keys [act! draw!] :as opts}]
  (doto (proxy [Actor] []
          (act [delta]
            (when act!
              (act! this delta))
            (let [^Actor this this]
              (proxy-super act delta)))
          (draw [batch parent-alpha]
            (when draw!
              (draw! this batch parent-alpha))))
    (actor/set-opts! opts)))
