import java.util.Random;
import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;

//used in methods for making random words
class Utils {
  /*
   * TEMPLATE: Fields: this.rand - Random Methods this.makeRandomWord — String
   * this.randomWordHelp(int count) – String
   */
  Random rand;

  Utils(Random rand) {
    this.rand = rand;
  }

  public String makeRandomWord() {
    return randomWordHelp(0);
  }

  public String randomWordHelp(int count) {
    int i = this.rand.nextInt(26);
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    if (count < 6) {
      return alphabet.substring(i, i + 1) + randomWordHelp(count + 1);
    }
    else {
      return "";
    }
  }
}

// describes the ZTypeWorld
class ZTypeWorld extends World {

  /*
   * TEMPLATE: Fields: this.wordList – ILoWord this.score - int this.rand - Random
   * 
   * 
   * Methods: this.makeScene() - WorldScene this.makeAFinalScene - WorldScene
   * this.worldEnds - WorldEnd this.onTick - ZTypeWorld this.onKeyEvent -
   * ZTypeWorld
   */

  ILoWord wordList;
  int score;
  Random rand;

  // starting scene
  WorldScene startingScene = new WorldScene(500, 500);

  ZTypeWorld(ILoWord wordList, int score) {
    this.wordList = wordList;
    this.score = score;
    this.rand = new Random();
  }

  ZTypeWorld(ILoWord wordList, int score, Random rand) {
    this.wordList = wordList;
    this.score = score;
    this.rand = rand;
  }

  // draw the scene in a given state
  public WorldScene makeScene() {
    return this.wordList.draw(this.startingScene.placeImageXY(
        new TextImage("Score: " + String.valueOf(this.score), 30, Color.GREEN), 400, 450));
  }

  public WorldScene makeAFinalScene() {
    return this.startingScene.placeImageXY(new TextImage("Womp Womp you lose", 40, Color.BLACK),
        250, 250);
  }

  public WorldEnd worldEnds() {
    if (this.wordList.outOfBounds()) {
      return new WorldEnd(true, this.makeAFinalScene());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // creates world on each tick
  public ZTypeWorld onTick() {
    IWord word = new InactiveWord(new Utils(this.rand).makeRandomWord());
    ILoWord list = this.wordList.filterOutEmpties();
    if (list.length() == this.wordList.length()) {
      return new ZTypeWorld(list.addToEnd(word, this.rand).move(), this.score, this.rand);
    }
    else {
      return new ZTypeWorld(list.addToEnd(word, this.rand).move(), this.score + 1, this.rand);
    }
  }

  public ZTypeWorld onKeyEvent(String key) {
    if (this.wordList.hasActive()) {
      return new ZTypeWorld(this.wordList.checkAndReduce(key), this.score, this.rand);
    }
    else {
      return new ZTypeWorld(this.wordList.checkAndReduceInactive(key), this.score, this.rand);
    }
  }

}

//represents a list of words
interface ILoWord {
  /*
   * TEMPLATE:
   * 
   * Methods:
   * 
   * this.hasActive() - boolean this.length() - int this.outOfBounds() - boolean
   * this.move() - ILoWord this.checkAndReduce(String letter) --ILoWord
   * this.addToEnd(IWord word) -- ILoWord this.filterOutEmpties()--ILoWord
   * this.draw(WorldScene scene) --ILoWord this.checkAndReduceInactive(String
   * letter) - ILoWord
   * 
   */

  // determines in ILoWord has an active word in it
  boolean hasActive();

  // returns the length of the ILoWord
  int length();

  // determines if any IWords are out of bounds
  boolean outOfBounds();

  // moves the list of Words down
  public ILoWord move();

  // removes first letter from strings that start with given letter in a ILoWord
  public ILoWord checkAndReduce(String letter);

  // adds given IWord to end of ILoWord
  public ILoWord addToEnd(IWord word, Random rand);

  // filters out empty IWords from a ILoWord
  public ILoWord filterOutEmpties();

  // draws this ILoWord onto a WorldScene
  public WorldScene draw(WorldScene scene);

  // checks and reduces the inactive word that matches letter
  public ILoWord checkAndReduceInactive(String letter);

}

//represents an empty list of words
class MtLoWord implements ILoWord {

  /*
   * TEMPLATE:
   * 
   * Methods:
   * 
   * this.hasActive() - boolean this.length() - int this.outOfBounds() - boolean
   * this.move() - ILoWord this.checkAndReduce(String letter) --ILoWord
   * this.addToEnd(IWord word) -- ILoWord this.filterOutEmpties()--ILoWord
   * this.draw(WorldScene scene) --ILoWord this.checkAndReduceInactive(String
   * letter) - ILoWord
   * 
   */

  // removes first letter from strings that start with given letter in a ILoWord
  public ILoWord checkAndReduce(String letter) {
    return new MtLoWord();
  }

  // adds given IWord to end of ILoWord
  public ILoWord addToEnd(IWord word, Random rand) {
    int num = rand.nextInt(40);
    if (num == 25) {
      return new ConsLoWord(word, new MtLoWord());
    }
    else {
      return this;
    }
  }

  // filters out empty IWords from a ILoWord
  public ILoWord filterOutEmpties() {
    return new MtLoWord();
  }

  // draws this ILoWord onto a WorldScene
  public WorldScene draw(WorldScene scene) {
    return scene;
  }

  // moves the list of Words down
  public ILoWord move() {
    return this;
  }

  // determines if any words are out of bounds
  public boolean outOfBounds() {
    return false;
  }

  // determines in ILoWord has an active word in it
  public boolean hasActive() {
    return false;
  }

  // reduces inactive words
  public ILoWord checkAndReduceInactive(String letter) {
    return this;
  }

  // returns the length of the ILoWord
  public int length() {
    return 0;
  }

}

class ConsLoWord implements ILoWord {

  /*
   * TEMPLATE:
   * 
   * Methods:
   * 
   * this.hasActive() - boolean this.length() - int this.outOfBounds() - boolean
   * this.move() - ILoWord this.checkAndReduce(String letter) --ILoWord
   * this.addToEnd(IWord word) -- ILoWord this.filterOutEmpties()--ILoWord
   * this.draw(WorldScene scene) --ILoWord this.checkAndReduceInactive(String
   * letter) - ILoWord
   * 
   * Fields: this.first this.rest
   * 
   */

  IWord first;
  ILoWord rest;

  ConsLoWord(IWord first, ILoWord rest) {
    this.first = first;
    this.rest = rest;
  }

  // removes first letter from strings that start with given letter in a ILoWord
  public ILoWord checkAndReduce(String letter) {
    if (this.first.equalIWord(letter)) {
      return new ConsLoWord(this.first.reduce(), this.rest.checkAndReduce(letter));
    }
    else {
      return new ConsLoWord(this.first, rest.checkAndReduce(letter));
    }
  }

  // reduces inactive words
  public ILoWord checkAndReduceInactive(String letter) {
    if (this.first.equalIWord(letter)) {
      return new ConsLoWord(this.first.reduceInactive(), this.rest);
    }
    else {
      return new ConsLoWord(this.first, rest.checkAndReduceInactive(letter));
    }
  }

  // adds given IWord to end of ILoWord
  public ILoWord addToEnd(IWord word, Random rand) {
    return new ConsLoWord(first, rest.addToEnd(word, rand));
  }

  // filters out empty IWords from a ILoWord
  public ILoWord filterOutEmpties() {
    if (this.first.isEmptyIWord()) {
      return this.rest.filterOutEmpties();
    }
    return new ConsLoWord(this.first, this.rest.filterOutEmpties());
  }

  // draws this ILoWord onto a WorldScene
  public WorldScene draw(WorldScene scene) {
    if (this.first.isEmptyIWord()) {
      return this.rest.draw(scene);
    }
    return this.rest.draw(this.first.drawIWord(scene));
  }

  // moves the list of Words down
  public ILoWord move() {
    return new ConsLoWord(this.first.move(), this.rest.move());
  }

  // determines if any words are out of bounds
  public boolean outOfBounds() {
    return this.first.outOfBounds() || this.rest.outOfBounds();
  }

  // determines in ILoWord has an active word in it
  public boolean hasActive() {
    return this.first.isActive() || this.rest.hasActive();
  }

  // returns the length of the ILoWord
  public int length() {
    return 1 + this.rest.length();
  }

}

//represents a word in the ZType game
interface IWord {

  // determines if any words are out of bounds
  boolean outOfBounds();

  // moves word down
  public IWord move();

  // builds a WorldScene with image of IWord
  public WorldScene drawIWord(WorldScene scene);

  // determines if IWord is empty;
  public boolean isEmptyIWord();

  // removes first letter from word in an IWord
  public IWord reduce();

  // checks if a InactiveWord starts with given letter
  public boolean equalIWord(String letter);

  // reduces inactive IWord
  public IWord reduceInactive();

  // used to check if any word in an ILoWord is active
  public boolean isActive();

}

abstract class AWord implements IWord {
  Color activeColor = Color.BLUE;
  Color inactiveColor = Color.RED;
  String word;
  int x;
  int y;

  AWord(String word) {
    this.word = word;
    this.x = new Random().nextInt(420) + 40;
    this.y = 0;
  }

  AWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
  }

  // checks if inActiveWord is empty and for end scene
  public boolean isEmptyIWord() {
    return this.word.isEmpty();
  }

  // checks if a InactiveWord starts with given letter
  public boolean equalIWord(String letter) {
    return this.word.startsWith(letter);
  }

  // determines if any words are out of bounds
  public boolean outOfBounds() {
    return this.y > 500;
  }
}

/*
 * Template
 * 
 * Fields: this.word --String this.x -- int this.y -- int
 * 
 * Methods: this.outOfBounds - boolean this.move - IWord this.reduceInactive -
 * IWord this.isActive - boolean this.drawIWord(WorldScene scene) -- WorldScene
 * this.isEmptyIWord() -- boolean this.reduce() -- IWord this.equalIWord(String
 * letter) -- boolean
 */

// represents an active word
class ActiveWord extends AWord {

  ActiveWord(String word) {
    super(word);
  }

  ActiveWord(String word, int x, int y) {
    super(word, x, y);
  }

  public IWord reduceInactive() {
    return this;
  }

  // draws ActiveWord on a World Scene
  public WorldScene drawIWord(WorldScene scene) {
    return scene.placeImageXY(new TextImage(this.word, 18, activeColor), this.x, this.y);
  }

  public IWord move() {
    return new ActiveWord(this.word, this.x, this.y + 2);
  }

  public IWord reduce() {
    return new ActiveWord(this.word.substring(1), this.x, this.y);
  }

  // determine if word is active
  public boolean isActive() {
    return true;
  }
}

/*
 * Template
 * 
 * Fields: this.word --String this.x -- int this.y -- int
 * 
 * Methods: this.outOfBounds - boolean this.move - IWord this.reduceInactive -
 * IWord this.isActive - boolean this.drawIWord(WorldScene scene) -- WorldScene
 * this.isEmptyIWord() -- boolean this.reduce() -- IWord this.equalIWord(String
 * letter) -- boolean
 */

//represents an inactive word in the ZType game
class InactiveWord extends AWord {

  InactiveWord(String word) {
    super(word);
  }

  InactiveWord(String word, int x, int y) {
    super(word, x, y);
  }

  public IWord reduceInactive() {
    return new ActiveWord(this.word.substring(1), this.x, this.y);
  }

  // draws an InactiveWord on a WorldScene
  public WorldScene drawIWord(WorldScene scene) {
    return scene.placeImageXY(new TextImage(this.word, 18, inactiveColor), this.x, this.y);
  }

  // moves word down
  public IWord move() {
    return new InactiveWord(this.word, this.x, this.y + 2);
  }

  public IWord reduce() {
    return this;
  }

  // determine if word is active
  public boolean isActive() {
    return false;
  }
}

//all examples and tests for ILoWord
class ExamplesWordLists {
  ILoWord empty = new MtLoWord();
  IWord activeNone = new ActiveWord("", 10, 10);
  IWord inactiveNone = new InactiveWord("", 300, 50);
  IWord activeHello = new ActiveWord("hello", 50, 250);
  IWord inactiveHello = new InactiveWord("Hello", 50, 80);
  IWord activeJava = new ActiveWord("Java", 400, 30);
  IWord inactiveJava = new InactiveWord("Java", 250, 400);
  IWord activeCode = new ActiveWord("code", 200, 200);
  IWord inactiveCode = new InactiveWord("code", 190, 380);
  ILoWord noneEmpty = new ConsLoWord(activeNone, empty);
  ILoWord hello = new ConsLoWord(activeHello, empty);
  ILoWord helloHello = new ConsLoWord(inactiveHello, hello);
  ILoWord noneJava = new ConsLoWord(inactiveNone, new ConsLoWord(inactiveJava, empty));
  ILoWord javaHelloHello = new ConsLoWord(activeJava, helloHello);
  ILoWord codeHello = new ConsLoWord(activeCode, hello);
  ILoWord noneHello = new ConsLoWord(inactiveNone, new ConsLoWord(activeHello, empty));
  ILoWord codeJava = new ConsLoWord(activeCode, new ConsLoWord(inactiveJava, empty));
  ILoWord noneCodeHelloJava = new ConsLoWord(inactiveNone,
      new ConsLoWord(activeCode, new ConsLoWord(activeHello, new ConsLoWord(inactiveJava, empty))));
  ILoWord noneCodeHelloHelloJava = new ConsLoWord(inactiveNone, new ConsLoWord(activeCode,
      new ConsLoWord(activeHello, new ConsLoWord(activeHello, new ConsLoWord(activeJava, empty)))));
  ILoWord javaCodeHello = new ConsLoWord(activeJava, new ConsLoWord(activeCode, hello));
  ILoWord codeHelloJava = new ConsLoWord(activeCode,
      new ConsLoWord(activeHello, new ConsLoWord(activeJava, empty)));
  ILoWord codeHelloJavaJava = new ConsLoWord(activeCode,
      new ConsLoWord(activeHello, new ConsLoWord(activeJava, new ConsLoWord(activeJava, empty))));
  ILoWord noneCodeHelloNoneJava = new ConsLoWord(activeNone,
      new ConsLoWord(activeCode, new ConsLoWord(activeHello,
          (new ConsLoWord(inactiveNone, new ConsLoWord(activeJava, empty))))));
  ILoWord noneCodeHelloNoneJavaHello = new ConsLoWord(activeNone,
      new ConsLoWord(activeCode, new ConsLoWord(activeHello, (new ConsLoWord(inactiveNone,
          new ConsLoWord(activeJava, new ConsLoWord(inactiveHello, empty)))))));
  ILoWord noneNoneCodeHelloHelloJava = new ConsLoWord(activeNone,
      new ConsLoWord(inactiveNone, new ConsLoWord(activeCode, new ConsLoWord(activeHello,
          new ConsLoWord(inactiveHello, new ConsLoWord(activeJava, empty))))));

  // ZTypeWorld examples
  ZTypeWorld outOfBoundsWorld = new ZTypeWorld(
      new ConsLoWord(new ActiveWord("ben", 600, 600), new MtLoWord()), 0, new Random(1));
  ZTypeWorld emptyWorld = new ZTypeWorld(empty, 0, new Random(1));
  ZTypeWorld nonEmptyWorld = new ZTypeWorld(this.codeHelloJava, 0, new Random(5));
  ZTypeWorld nonEmptyWorld2 = new ZTypeWorld(this.noneCodeHelloNoneJavaHello, 0, new Random(10));

  // active words are blue and inactive words are red
  WorldScene world = new WorldScene(500, 500)
      .placeImageXY(new TextImage("Score: " + "0", 30, Color.GREEN), 400, 450);
  WorldImage textActiveHello = new TextImage("hello", 18, Color.BLUE);
  WorldImage textInactiveHello = new TextImage("Hello", 18, Color.RED);
  WorldImage textActiveJava = new TextImage("Java", 18, Color.BLUE);
  WorldImage textInactiveJava = new TextImage("Java", 18, Color.RED);
  WorldImage textActiveCode = new TextImage("code", 18, Color.BLUE);
  WorldImage textInactiveCode = new TextImage("code", 18, Color.RED);
  WorldImage textActiveNone = new TextImage("", 18, Color.BLUE);
  WorldImage textInactiveNone = new TextImage("", 18, Color.RED);

  WorldScene codeOnWorld = this.world.placeImageXY(textActiveCode, 200, 200);
  WorldScene helloCodeOnWorld = this.codeOnWorld.placeImageXY(textActiveHello, 50, 250);
  WorldScene javaHelloCodeOnWorld = this.helloCodeOnWorld.placeImageXY(textActiveJava, 400, 30);
  WorldScene helloJavaHelloCodeOnWorld = this.javaHelloCodeOnWorld.placeImageXY(textInactiveHello,
      50, 80);
  WorldScene javaJavaHelloCodeOnWorld = this.javaHelloCodeOnWorld.placeImageXY(textActiveJava, 400,
      30);

  // tests checkAndReduce
  boolean testcheckAndReduce(Tester t) {
    return t.checkExpect(this.empty.checkAndReduce("h"), this.empty)
        && t.checkExpect(this.hello.checkAndReduce("h"),
            new ConsLoWord(new ActiveWord("ello", 50, 250), this.empty))
        && t.checkExpect(this.hello.checkAndReduce("H"), this.hello)
        && t.checkExpect(this.helloHello.checkAndReduce("h"),
            new ConsLoWord(inactiveHello,
                new ConsLoWord(new ActiveWord("ello", 50, 250), this.empty)))
        && t.checkExpect(this.javaHelloHello.checkAndReduce("h"),
            new ConsLoWord(activeJava,
                new ConsLoWord(inactiveHello,
                    new ConsLoWord(new ActiveWord("ello", 50, 250), this.empty))))
        && t.checkExpect(this.noneJava.checkAndReduce("j"), noneJava);
  }

  // tests addToEnd
  boolean testAddToEnd(Tester t) {
    Random rand = new Random(1);
    return t.checkExpect(this.javaCodeHello.addToEnd(new ActiveWord("code", 1, 2), rand),
        new ConsLoWord(activeJava,
            new ConsLoWord(activeCode,
                new ConsLoWord(this.activeHello,
                    new ConsLoWord(new ActiveWord("code", 1, 2), empty)))))
        && t.checkExpect(this.hello.addToEnd(new InactiveWord("code", 2, 3), new Random(2)),
            this.hello)
        && t.checkExpect(this.javaCodeHello.addToEnd(new ActiveWord("code", 1, 2), new Random(3)),
            this.javaCodeHello);

  }

  // tests filterOutEmpties
  boolean testFilterOutEmpties(Tester t) {
    return t.checkExpect(this.empty.filterOutEmpties(), this.empty)
        && t.checkExpect(this.noneEmpty.filterOutEmpties(), this.empty)
        && t.checkExpect(this.noneCodeHelloNoneJava.filterOutEmpties(), codeHelloJava)
        && t.checkExpect(this.codeHelloJava.filterOutEmpties(), this.codeHelloJava);

  }

  // tests draw
  boolean testDraw(Tester t) {
    return t.checkExpect(this.empty.draw(this.world), this.world)
        && t.checkExpect(this.noneEmpty.draw(this.world), this.world)
        && t.checkExpect(this.noneCodeHelloNoneJava.draw(this.world), this.javaHelloCodeOnWorld)
        && t.checkExpect(this.noneCodeHelloNoneJavaHello.draw(this.world),
            this.helloJavaHelloCodeOnWorld)
        && t.checkExpect(this.codeHelloJavaJava.draw(this.world), this.javaJavaHelloCodeOnWorld)
        && t.checkExpect(this.hello.draw(this.codeOnWorld), this.helloCodeOnWorld);
  }

  // tests makeRandomWord
  boolean testMakeRandomWord(Tester t) {
    Random rand = new Random(1);
    Random rand2 = new Random(2);

    return t.checkExpect(new Utils(rand).makeRandomWord(), "rahjmy")
        && t.checkExpect(new Utils(rand2).makeRandomWord(), "sgavre");
  }

  // tests randomWordHelp
  boolean testRandomWordHelp(Tester t) {
    Random rand = new Random(1);
    Random rand2 = new Random(2);

    return t.checkExpect(new Utils(rand).randomWordHelp(0), "rahjmy")
        && t.checkExpect(new Utils(rand2).makeRandomWord(), "sgavre");
  }

  // tests isEmptyIWord
  boolean testIsEmptyIWord(Tester t) {
    return t.checkExpect(activeNone.isEmptyIWord(), true)
        && t.checkExpect(activeHello.isEmptyIWord(), false);
  }

  // tests reduce
  boolean testReduce(Tester t) {
    return t.checkExpect(activeHello.reduce(), (new ActiveWord("ello", 50, 250)))
        && t.checkExpect(inactiveHello.reduce(), this.inactiveHello);
  }

  // tests checkAndReduceInactive
  boolean testCheckAndReduceInactive(Tester t) {
    return t.checkExpect(this.hello.checkAndReduceInactive("h"), this.hello)
        && t.checkExpect(this.helloHello.checkAndReduceInactive("H"),
            new ConsLoWord(new ActiveWord("ello", 50, 80), this.hello));
  }

  // tests move in ILoWord and IWord
  boolean testMove(Tester t) {
    return t.checkExpect(this.hello.move(), new ConsLoWord(new ActiveWord("hello", 50, 252), empty))
        && t.checkExpect(this.activeHello.move(), new ActiveWord("hello", 50, 252));
  }

  // tests hasActive and isActive
  boolean testActive(Tester t) {
    return t.checkExpect(this.noneEmpty.hasActive(), true)
        && t.checkExpect(this.noneJava.hasActive(), false)
        && t.checkExpect(this.inactiveJava.isActive(), false)
        && t.checkExpect(this.activeJava.isActive(), true);
  }

  // tests length method
  boolean testLength(Tester t) {
    return t.checkExpect(this.noneEmpty.length(), 1) && t.checkExpect(this.helloHello.length(), 2);
  }

  // tests ILoWord and IWord outOfBounds method
  boolean testOutOfBounds(Tester t) { //
    return t.checkExpect(this.noneEmpty.outOfBounds(), false)
        && t.checkExpect(
            new ConsLoWord(new ActiveWord("ben", 600, 600), new MtLoWord()).outOfBounds(), true)
        && t.checkExpect(new ActiveWord("ben", 600, 600).outOfBounds(), true)
        && t.checkExpect(activeHello.outOfBounds(), false);
  }

  // tests reduceInactive
  boolean testReduceInactive(Tester t) {
    return t.checkExpect(inactiveHello.reduceInactive(), (new ActiveWord("ello", 50, 80)))
        && t.checkExpect(activeHello.reduceInactive(), this.activeHello);
  }

  // tests drawIWord
  boolean testDrawIWord(Tester t) {
    WorldScene world = new WorldScene(500, 500);
    return t.checkExpect(activeHello.drawIWord(world), world.placeImageXY(textActiveHello, 50, 250))
        && t.checkExpect(activeNone.drawIWord(world), world.placeImageXY(textActiveNone, 10, 10));
  }

  // tests equalIWord
  boolean testEqualIWord(Tester t) {
    return t.checkExpect(this.activeCode.equalIWord("t"), false)
        && t.checkExpect(this.activeJava.equalIWord("J"), true)
        && t.checkExpect(this.activeHello.equalIWord("h"), true);
  }

  // tests makeWorld
  boolean testMakeWorld(Tester t) {
    return t.checkExpect(this.emptyWorld.makeScene(), world)
        && t.checkExpect(this.nonEmptyWorld.makeScene(), this.javaHelloCodeOnWorld)
        && t.checkExpect(this.nonEmptyWorld2.makeScene(), this.helloJavaHelloCodeOnWorld);
  }

  // tests makeFinalScene
  boolean testMakeFinalScene(Tester t) {
    return t.checkExpect(nonEmptyWorld.makeAFinalScene(), new WorldScene(500, 500)
        .placeImageXY(new TextImage("Womp Womp you lose", 40, Color.BLACK), 250, 250));
  }

  // tests worldEnds
  boolean testWorldEnds(Tester t) {
    return t.checkExpect(outOfBoundsWorld.worldEnds(),
        new WorldEnd(true, outOfBoundsWorld.makeAFinalScene()));
  }

  // tests onTick
  boolean testOnTick(Tester t) {
    return t.checkExpect(this.emptyWorld.onTick(), this.emptyWorld)
        && t.checkExpect(this.outOfBoundsWorld.onTick(),
            new ZTypeWorld(new ConsLoWord(new ActiveWord("ben", 600, 602), new MtLoWord()), 0));
  }

  // test onKeyEvent
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(this.outOfBoundsWorld.onKeyEvent("b"),
        new ZTypeWorld(new ConsLoWord(new ActiveWord("en", 600, 600), new MtLoWord()), 0))
        && t.checkExpect(this.outOfBoundsWorld.onKeyEvent("k"), this.outOfBoundsWorld);
  }

  // runs program
  boolean testBigBang(Tester t) {
    ZTypeWorld world = new ZTypeWorld(this.empty, 0);
    int worldWidth = 500;
    int worldHeight = 500;
    double tickRate = 0.10;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }

}
