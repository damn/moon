(ns clojure.gdx.scene2d.actor
  (:refer-clojure :exclude [name])
  (:require [clojure.gdx.math.vector2 :as vector2]
            [clojure.gdx.scene2d.touchable :as touchable]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.utils.click-listener :as click-listener]
            [clojure.gdx.utils.align :as align]
            [clojure.scene2d.actor :as actor]
            clojure.scene2d.event)
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Event)
           (com.badlogic.gdx.scenes.scene2d.ui Button
                                               Label
                                               Window)))

(extend-type Event
  clojure.scene2d.event/Event
  (stage [event]
    (.getStage event)))

(defn- ui-type->class [k]
  (case k
    :ui/window com.badlogic.gdx.scenes.scene2d.ui.Window))

(defn- button-class? [actor]
  (some #(= Button %) (supers (class actor))))

(extend-type Actor
  clojure.scene2d.actor/Actor
  (set-opts! [actor opts]
    (when-let [user-object (:actor/user-object opts)]
      (actor/set-user-object! actor user-object))

    (when (:actor/position opts)
      (let [[x y align] (:actor/position opts)]
        (if align
          (actor/set-position! actor x y (align/k->value align))
          (actor/set-position! actor [x y]))))

    (when (contains? opts :actor/visible?)
      (actor/set-visible! actor (:actor/visible? opts)))

    (when-let [touchable (:actor/touchable opts)]
      (actor/set-touchable! actor (case touchable
                                    :touchable/disabled touchable/disabled)))

    (when-let [name (:actor/name opts)]
      (actor/set-name! actor name))

    (when-let [listeners (:actor/listeners opts)]
      (doseq [listener listeners]
        (actor/add-listener! actor listener))))

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

  (stage->local-coordinates [actor xy]
    (vector2/->clj (Actor/.stageToLocalCoordinates actor (vector2/->java xy))))

  (add-listener! [actor [listener-k listener-params]]
    (.addListener actor
                  (case listener-k
                    :listener/change (let [f listener-params]
                                       (change-listener/create f))
                    :listener/text-tooltip (let [[tooltip skin] listener-params]
                                             (text-tooltip/create tooltip skin))
                    :listener/click (let [f listener-params]
                                      (click-listener/create f)))))

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
     (.setPosition actor x y align)))

  (set-visible! [actor visible?]
    (.setVisible actor visible?))

  (set-touchable! [actor touchable]
    (.setTouchable actor touchable))

  (visible? [actor]
    (.isVisible actor))

  (hit [actor [x y] touchable?]
    (.hit actor x y touchable?))

  (remove! [actor]
    (.remove actor))

  (parent [actor]
    (.getParent actor))

  (find-ancestor
    [actor ui-type-k]
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
    (when (instance? Label actor)
      (when-let [p (actor/parent actor)]
        (when-let [p (actor/parent p)]
          (and (instance? Window actor)
               (= (Window/.getTitleLabel p) actor)))))))

(defn create
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
