(ns clojure.gdx.scene2d.actor
  (:refer-clojure :exclude [name])
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.scene2d.touchable :as touchable]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.utils.click-listener :as click-listener]
            [clojure.gdx.utils.align :as align])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Button
                                               Label
                                               Window)))

(defn- ui-type->class [k]
  (case k
    :ui/window com.badlogic.gdx.scenes.scene2d.ui.Window))

(defn- button-class? [actor]
  (some #(= Button %) (supers (class actor))))

(defn name [^Actor actor]
  (.getName actor))

(defn x [^Actor actor]
  (.getX actor))

(defn y [^Actor actor]
  (.getY actor))

(defn width [^Actor actor]
  (.getWidth actor))

(defn height [^Actor actor]
  (.getHeight actor))

(defn stage->local-coordinates [^Actor actor xy]
  (vector2/->clj (Actor/.stageToLocalCoordinates actor (vector2/->java xy))))

(defn add-listener! [^Actor actor [listener-k listener-params]]
  (.addListener actor
                (case listener-k
                  :listener/change (let [f listener-params]
                                     (change-listener/create f))
                  :listener/text-tooltip (let [[tooltip skin] listener-params]
                                           (text-tooltip/create tooltip skin))
                  :listener/click (let [f listener-params]
                                    (click-listener/create f)))))

(defn user-object [^Actor actor]
  (.getUserObject actor))

(defn stage [^Actor actor]
  (.getStage actor))

(defn set-name! [^Actor actor name]
  (.setName actor name))

(defn set-user-object! [^Actor actor object]
  (.setUserObject actor object))

(defn set-position!
  ([^Actor actor [x y]]
   (.setPosition actor x y))
  ([^Actor actor x y align]
   (.setPosition actor x y align)))

(defn set-visible! [^Actor actor visible?]
  (.setVisible actor visible?))

(defn set-touchable! [^Actor actor touchable]
  (.setTouchable actor touchable))

(defn visible? [^Actor actor]
  (.isVisible actor))

(defn hit [^Actor actor [x y] touchable?]
  (.hit actor x y touchable?))

(defn remove! [^Actor actor]
  (.remove actor))

(defn parent [^Actor actor]
  (.getParent actor))

(defn find-ancestor
  [actor ui-type-k]
  (if-let [parent (parent actor)]
    (if (instance? (ui-type->class ui-type-k) parent)
      parent
      (find-ancestor parent ui-type-k))
    (throw (Error. (str "Actor has no parent window " actor)))))

(defn button? [actor]
  (or (button-class? actor)
      (and (parent actor)
           (button-class? (parent actor)))))

; FIXME does not work
(defn window-title-bar? [actor]
  (when (instance? Label actor)
    (when-let [p (parent actor)]
      (when-let [p (parent p)]
        (and (instance? Window actor)
             (= (Window/.getTitleLabel p) actor))))))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))

(defn set-opts! [actor opts]
  (when-let [user-object (:actor/user-object opts)]
    (set-user-object! actor user-object))

  (when (:actor/position opts)
    (let [[x y align] (:actor/position opts)]
      (if align
        (set-position! actor x y (align/k->value align))
        (set-position! actor [x y]))))

  (when (contains? opts :actor/visible?)
    (set-visible! actor (:actor/visible? opts)))

  (when-let [touchable (:actor/touchable opts)]
    (set-touchable! actor (case touchable
                            :touchable/disabled touchable/disabled)))

  (when-let [name (:actor/name opts)]
    (set-name! actor name))

  (when-let [listeners (:actor/listeners opts)]
    (doseq [listener listeners]
      (add-listener! actor listener))))

(defmulti create :type)

(defmethod create :ui/actor
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
    (set-opts! opts)))
